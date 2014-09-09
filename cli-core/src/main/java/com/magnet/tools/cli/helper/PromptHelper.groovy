/*
 * Copyright (c) 2014 Magnet Systems, Inc.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.magnet.tools.cli.helper

import com.magnet.tools.cli.core.Shell
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.config.ConfigLexicon
import com.magnet.tools.utils.AnsiHelper
import com.magnet.tools.utils.StringHelper
import groovy.util.logging.Slf4j
import jline.JlineInterruptException
import org.codehaus.groovy.runtime.StackTraceUtils

import java.util.regex.Pattern
import static com.magnet.tools.cli.messages.HelperMessages.*

/**
 * helper methods facilitating interview process
 * Some of which inpired from Grails template plugins
 */
@Slf4j
class PromptHelper {

  private static final PromptValidator DEFAULT_VALIDATOR = new NotEmptyPromptValidator()
  private static final String LINE_SEP = System.getProperty("line.separator")

  // http://stackoverflow.com/questions/5205339/regular-expression-matching-fully-qualified-java-classes
  public static final Pattern CLASS_NAME_PATTERN = ~/[\p{L}_][\p{L}\p{N}_]*/ // without $
  public static final Pattern PACKAGE_NAME_PATTERN = ~/([\p{L}_][\p{L}\p{N}_]*\.)*[\p{L}_][\p{L}\p{N}_]*/ // without $
  public static final Pattern MAVEN_ID_VALIDATOR = ~/[A-Za-z0-9_+.\\-]+/  // from  DefaultModelValidator

  private final Shell context
  private final Map questionnaire

  //
  // questionnaire keys
  //

  /**
   * The type of answer, see *_TYPE constants
   */
  public static final String TYPE_KEY = "type"
  /**
   * password answer, the user will be prompted for a password, and the console will mask the answer
   */
  public static final String PASSWORD_TYPE = "password"
  /**
   * string answer, this is the default type if 'type' is ommitted.
   */
  public static final String STRING_TYPE = "string"

  /**
   * resource answer, a file on the FS or a URL,
   * similar to {@link #STRING_TYPE} except that it will expand the file path if it is on the FS
   */
  public static final String RESOURCE_TYPE = "resource"
  /**
   * options answer, user will be prompted to choose amongst a list of options, by selecting a number
   */
  public static final String OPTIONS_TYPE = "options"
  /**
   * key for list of options to choose from in questionnaire
   */
  public static final String OPTIONS_VALUE_KEY = "options"
  /**
   * boolean answer, user will be prompted for yes or no
   */
  public static final String BOOLEAN_TYPE = "boolean"

  /**
   * Whether to re-type the password answer, and compare with first password answer
   */
  public static final String PASSWORD_CONFIRM_KEY = "confirm"
  /**
   *  optional default value for variable that will appear during prompt. If user press Enter, the default value is picked
   *  Note that the value associated with this entry can be a closure, which will be evaluated against the current set
   *  of responses collected through the command line and the on-going interview.
   */
  public static final String DEFAULT_VALUE_KEY = "defaultValue"

  /**
   * Optional key which associated value, typically a closure, will be evaluated to determine whether
   * to continue the questionnaire and accept all subsequent default values (when true).
   */
  public static final String SKIP_OTHERS_IF_KEY = 'skipOthersIf'

  /**
   * Optional key which associated value is a wizard instance which will help coming up with an appropriate value
   * A wizard typically show more details or ask questions and perform operation to assist the user.
   */
  public static final String WIZARD_KEY = 'wizard'
  /**
   *  Indicate a constant value, not modifiable by the user. This only appears during interview summary.
   */
  public static final String CONSTANT_TYPE = "constant"

  /**
   * optional
   */
  public static final String QUESTION_KEY = "question"

  /**
   * optional validator of type {@link PromptValidator}
   */
  public static final String VALIDATOR_KEY = "validator"

  /**
   * Constructor
   * @param context command context
   * @param questionnaire questionnaire specification
   */
  PromptHelper(Shell context, Map<String, Object> questionnaire) {
    this.context = context
    this.questionnaire = questionnaire
  }

  /**
   * Complete a set of responses with default values for question non-answered, and constants values.
   * @param enteredValues current set of responses
   * @return copy of enteredValues completed with defaults.
   */
  Map<String, Object> completeWithDefaults(final Map<String, Object> enteredValues) {
    // add non-constant default values:
    Map<String, Object> result = enteredValues.clone() // shallow copy, so we should ensure values are immutable
    adaptValues(result) // convert values according to spec
    questionnaire.each { k, v ->
      String type = evaluate(enteredValues + result, v[TYPE_KEY])
      if (v?.containsKey(DEFAULT_VALUE_KEY)) {
        if (!enteredValues.containsKey(k) || type == CONSTANT_TYPE) {
          def defaultValue = evaluate(result, v[DEFAULT_VALUE_KEY])
          if (type == OPTIONS_TYPE) {
            defaultValue = v[OPTIONS_VALUE_KEY].get(defaultValue - 1)
          }
          result.put(k, defaultValue)
        }
      } else if (v?.containsKey(WIZARD_KEY)) {
        if (!enteredValues.containsKey(k) || evaluate(enteredValues + result, type) == CONSTANT_TYPE) {
          PromptHelperWizard wizard = v[WIZARD_KEY]
          if (wizard) {
            wizard.interactive = false
            wizard.setValues(result)
            String value = wizard.start()
            result.put(k, value)
          }
        }
      }
    }
    validate(result)


    return result

  }

  /**
   * List of variables not answered so far, no including those with default values
   * @param enteredValues
   * @return
   */
  Collection<String> getMissingRequiredVariables(Collection<String> enteredValues) {
    return (questionnaire.keySet().findAll { k -> !questionnaire[k].containsKey(DEFAULT_VALUE_KEY) }) - enteredValues
  }

  /**
   * Start a questionnaire conditionally,
   *
   * if interactive is set, always start a questionnaire
   * if non-interactive fill up variables with defaults, and fail-fast if missing properties
   * @param interactive whether prompt is interactive
   * @param promptHelper promptHelper
   * @param enteredValues collected key value pairs collected so far
   * @param confirm whether to confirm the set of choice
   * @return completed response, or null if the questionnaire exited early
   * @throws IncompleteQuestionnaireException if the questionnaire is incomplete or exited early
   * @throws IllegalArgumentException if the argument are invalid
   */
  Map<String, Object> complete(boolean interactive, Map<String, Object> enteredValues, boolean confirm = true)
      throws IncompleteQuestionnaireException, IllegalArgumentException {

    Map<String, Object> responses
    responses = interactive ? start(enteredValues, confirm) : completeWithDefaults(enteredValues)

    // verify
    Collection<String> missingVariables = getMissingRequiredVariables(responses.keySet())
    if (missingVariables) {
      throw new IncompleteQuestionnaireException(
          getMessage(MISSING_REQUIRED_PROPERTIES, missingVariables))
    }

    return responses

  }

  /**
   * validate all values , by invoking their associated prompt validator (if any)
   * @param values values to validate
   * @throw IllegalArgumentException at least one of the values is invalid
   */
  void validate(Map<String, Object> values) {
    if (null == values) {
      return
    }
    for (key in values.keySet()) {
      if (!questionnaire.containsKey(key)) {
        continue
      }
      Map spec = questionnaire[key]
      PromptValidator validator = spec.containsKey(VALIDATOR_KEY) ? evaluate(values, spec[VALIDATOR_KEY]) : DEFAULT_VALIDATOR
      try {
        def val = values[key]?.toString()
        validator.validate(val)
      } catch (IllegalArgumentException e) {
        log.error("Invalid entry for $key", e)
        throw new IncompleteQuestionnaireException(getMessage(INVALID_ENTRY_FOR_KEY, key, e.getMessage()))
      }
    }

  }
  /**
   * Start a questionnaire
   * @param enteredValues map of values keyed by their associated variables names that are already populated
   * @param confirm whether to prompt for confirmation at the end of the questionnaire
   * @return map of values keyed by their associated variable names
   * @throws IncompleteQuestionnaireException if the questionnaire is incomplete or exited early
   * @throws IllegalArgumentException if the argument are invalid
   */
  private Map<String, Object> start(Map<String, Object> enteredValues, boolean confirm = true)
      throws IncompleteQuestionnaireException, IllegalArgumentException {

    def result = promptForVariablesValues(enteredValues)

    def consolidatedValues = enteredValues + result

    if (confirm) {
      context.getWriter().write(CommonMessages.summary())
      context.getWriter().write(LINE_SEP)
      consolidatedValues.each { k, v ->
        def type = evaluate(consolidatedValues, questionnaire[k]?.get(TYPE_KEY))
        if (type == CONSTANT_TYPE) {
          // WON-8845 - do not show irrelevant info (i.e. constant or unused)
          context.trace("$k = $v ${StringHelper.f(CommonMessages.constantOrUnused())}")
        } else {
          context.getWriter().println("$k = " + (type == PASSWORD_TYPE ? "*".multiply(v.size()) : v))
        }
      }
      context.getWriter().flush()
      if (promptYesOrNo(context, CommonMessages.isItCorrect(), true)) {
        return consolidatedValues;
      } else {
        throw new IncompleteQuestionnaireException(getMessage(EXITING_QUESTIONNAIRE_DO_RETRY))
      }
    }
    return consolidatedValues;
  }

  /**
   * Prompt for values for a set of variables
   * @param enteredValues current values keyed by their associated variable names
   * @return map of values keyed by their associated variables, or null if the questionnaire is aborted
   * @throws IncompleteQuestionnaireException if the questionnaire is incomplete or exited early
   * @throws IllegalArgumentException if the argument are invalid
   */
  private Map<String, Object> promptForVariablesValues(Map<String, Object> enteredValues)
      throws IncompleteQuestionnaireException,
          IllegalArgumentException {
    // Validate entered values
    validate(enteredValues)

    Collection<String> variables = questionnaire.keySet() - enteredValues.keySet()

    def result = [:]

    for (String it : variables) {

      def spec = questionnaire.get(it)
      def question = spec[QUESTION_KEY] ?: it + " ?"

      boolean retryQuestion = true
      while (retryQuestion) {  // no do while in groovy :(
        retryQuestion = false
        try {

          PromptHelperWizard wizard = spec[WIZARD_KEY]
          if (wizard) {
            String needsWizard = CommonMessages.needsWizard()
            boolean needsHelp = promptYesOrNo(context, "$question => " + needsWizard, false)
            if (needsHelp) {
              wizard.setValues(enteredValues + result)
              result[it] = wizard.start()
              if (result[it]) {
                break
              }
            }
          }
          String type = evaluate(enteredValues + result, spec[TYPE_KEY])
          switch (type?.toLowerCase()) {
          // The input of the type RESOURCE_TYPE are expanded to their full path (see WON-7975)
            case RESOURCE_TYPE:
              String source = prompt(context, question, evaluate(enteredValues + result, spec[DEFAULT_VALUE_KEY]))
              result[it] = expandResourceReference(source)
              break
            case STRING_TYPE:
              result[it] = prompt(context, question, evaluate(enteredValues + result, spec[DEFAULT_VALUE_KEY]))
              break

            case PASSWORD_TYPE:
              String password, password2
              boolean confirmed = false
              int retry = 0
              while (!confirmed && retry++ <= 3) {
                password = promptForPassword(context, question, '*' as Character)
                if (spec[PASSWORD_CONFIRM_KEY]) {
                  password2 = promptForPassword(context, getMessage(PLEASE_CONFIRM_PASSWORD), '*' as Character)
                  confirmed = (password == password2)
                  if (confirmed) {
                    break
                  }
                  context.info(StringHelper.b(getMessage(MISMATCH_ANSWER_DO_RETRY)))
                  context.getWriter().write(LINE_SEP)
                } else {
                  confirmed = true
                }
              }
              if (!confirmed) {
                throw new IncompleteQuestionnaireException(CommonMessages.commandAborted())
              }
              result[it] = password
              break

            case BOOLEAN_TYPE:
              result[it] = promptYesOrNo(context, question, evaluate(enteredValues + result, spec[DEFAULT_VALUE_KEY]) ?: false).toString()
              break

            case OPTIONS_TYPE:
              int index = promptOptions(context, question, evaluate(enteredValues + result, spec[DEFAULT_VALUE_KEY]) ?: 1, spec[OPTIONS_VALUE_KEY])
              // some option contains description, so we split the string and pick the first token, which is assumed to be the actual option value
              result[it] = ((List) spec[OPTIONS_VALUE_KEY]).getAt(index).trim().split()[0]
              break
            case CONSTANT_TYPE:
              // pass it, not modifiable , and not promptable
              if (!spec.containsKey(DEFAULT_VALUE_KEY)) {
                throw new IllegalArgumentException(getMessage(CONSTANT_TYPE_REQUIRES_DEFAULT, CONSTANT_TYPE, DEFAULT_VALUE_KEY))
              }
              result[it] = evaluate(enteredValues + result, spec[DEFAULT_VALUE_KEY])
              break;
            default:
              throw new IllegalArgumentException(getMessage(UNKNOWN_TYPE_FOR_QUESTION, evaluate(enteredValues + result, spec[TYPE_KEY]), question))
          }

          def v = spec.containsKey(VALIDATOR_KEY) ? evaluate(enteredValues + result, spec[VALIDATOR_KEY]) : DEFAULT_VALIDATOR
          if (v) {
            v.validate(result[it]?.toString())
          } else {
            DEFAULT_VALIDATOR.validate(result[it]?.toString())
          }
          if (spec[SKIP_OTHERS_IF_KEY]) {
            boolean skipOthers = evaluate(enteredValues + result, spec[SKIP_OTHERS_IF_KEY])
            if (skipOthers) {
              return completeWithDefaults(enteredValues + result)
            }
          }
        } catch (IllegalArgumentException iae) {
          log.warn("Validation failed: ", StackTraceUtils.sanitize(iae))
          context.getWriter().println(AnsiHelper.renderError(iae.getMessage()))
          retryQuestion = promptYesOrNo(context, getMessage(INVALID_ENTRY_DO_RETRY, result[it] ?: ""), true)
          if (!retryQuestion) {
            throw new IncompleteQuestionnaireException(CommonMessages.commandAborted())
          }
        } catch (JlineInterruptException j) {
          throw new IncompleteQuestionnaireException(CommonMessages.commandAborted())
        }  catch (Throwable e) {
          log.error("Could not get variable", StackTraceUtils.sanitize(e))
          throw new IncompleteQuestionnaireException(
              getMessage(ABORTING_QUESTION_WITH_ERROR, "(${e.class.getSimpleName()} : ${e.getMessage()})"))
        }
      } // while retry question
    } // loop through variables
    return result;
  }

/**
 * Given the current set of responses, evaluate closure value
 * @param currentResponses current set of values entered during interview, it will be injected inside the <code>value</code> parameter if it is a closure
 * @param value a value, or a closure. In the latter case, the <code>currentValues</code> is injected inside the closure
 * @return the evaluation of the <code>value</code> is a closure
 */
  private static def evaluate(Map<String, Object> currentResponses, def value) {
    if (value instanceof Closure && currentResponses != null) {
      return ((Closure) value).call(currentResponses)
    }
    return value;
  }
/**
 * Prompt for a particular question
 * @param context command context command context
 * @param message question
 * @param defaultValue optional default value
 * @return answer for question
 */
  static String prompt(Shell context, String message, String defaultValue = null) {
    return readLine(context, message, defaultValue)
  }

/**
 * Prompt for options choice
 * @param context command context
 * @param message question
 * @param options list of options to choose from
 * @return option chosen (index in the proposed list)
 */
  static int promptOptions(Shell context, String message, List<String> options = []) {
    promptOptions(context, message, 0, options)
  }

/**
 * Similar to {@link #promptOptions(Shell, java.lang.String)} but
 * include a default value
 * @param context command context
 * @param message question
 * @param options list of options to choose from
 * @return chosen option (index in the proposed list)
 * @param defaultValue default value (index+1 in the proposed list, it will appear as the default value)
 * @return
 */
  static int promptOptions(Shell context, String message, int defaultValue, List options = []) {
    String consoleMessage = message
    consoleMessage += "${LINE_SEP}    " + getMessage(SELECT_AN_OPTION)
    options.eachWithIndex { option, index ->
      consoleMessage += "${LINE_SEP}     (${index + 1}): ${option}"
    }
    try {
      def range = 0..options.size() - 1
      int choice = Integer.parseInt(readLine(context, consoleMessage + StringHelper.LINE_SEP + enterYourChoice(), defaultValue))
      if (choice == 0) {
        throw new Exception(getMessage(NO_OPTION_PROVIDED))
      }
      choice--
      if (range.containsWithinBounds(choice)) {
        return choice
      } else {
        throw new IllegalArgumentException(getMessage(INVALID_OPTION, choice + 1))
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(getMessage(INVALID_OPTION_WITH_ERROR, e.getMessage()), e)
    }
  }


/**
 * Similar to {@link #promptOptions(Shell, java.lang.String)} but
 * @param context command context
 * @param message question
 * @param defaultValue  index of the default value in the propsed list
 * @param options list of options to choose from
 * @return chosen options (list of indexes in the proposed list)
 */
  static List<Integer> promptMultiOptions(Shell context, String message, Integer defaultValue, List options = []) {
    final String ALL_SIGN = "*"
    String consoleMessage = message
    consoleMessage += "${LINE_SEP}    " + getMessage(SELECT_OPTIONS, ALL_SIGN)
    options.eachWithIndex { option, index ->
      consoleMessage += "${LINE_SEP}     (${index + 1}): ${option}"
    }
    if (options.size() > 1) {
      consoleMessage += "${LINE_SEP}     (${ALL_SIGN}): " + getMessage(SELECT_ALL_ABOVE)
    }

    def range = 0..options.size() - 1
    String input = readLine(context, consoleMessage + StringHelper.LINE_SEP + enterYourChoice(), defaultValue == -1 ? ALL_SIGN : defaultValue)
    if (!input) {
      return defaultValue == -1 ? range : [defaultValue]
    }
    String[] choices = input.split(",")
    List<Integer> result = new ArrayList<Integer>()
    for (String s : choices) {
      if (s.trim().equals(ALL_SIGN)) {
        return range
      }
      Integer oneChoice
      try {
        oneChoice = Integer.parseInt(s)
        oneChoice--
        if (range.containsWithinBounds(oneChoice)) {
          result.add(oneChoice)
        } else {
          throw new IllegalArgumentException(getMessage(INVALID_OPTION, oneChoice + 1))
        }
      } catch (NumberFormatException e) {

      }
    }

    return result.sort();
  }

/**
 * Prompt for a yes or no answer
 * @param context command context
 * @param message question
 * @param defaultValue optional default value
 * @return true if yes, false, if no
 */
  static boolean promptYesOrNo(Shell context, String message, boolean defaultValue = false) {
    String consoleVal = readLine(context, message + " " + yOrN(), defaultValue ? 'y' : 'n')
    if (consoleVal) {
      return consoleVal.toLowerCase().startsWith('y')
    }
    return defaultValue
  }

/**
 * Prompt for a yes or no answer and require an specific response
 *
 * @param context command context
 * @param message question
 * @param defaultValue optional default value
 * @return true if yes, false, if no
 */
  static boolean promptYesOrNoRequired(Shell context, String message) {
    boolean goodResponse = false;
    String resultValue = null
    while (!goodResponse) {
      resultValue = readLine(context, "$message " + yOrN())
      if (resultValue == null) {
        continue;
      }
      resultValue = resultValue.toLowerCase()
      if (['yes','y','no', 'n'].contains(resultValue)) {
        goodResponse = true
      }
    }
    return resultValue?.startsWith("y")
  }

/**
 * Prompt for a password
 * @param context command context
 * @param message prompt message
 * @param mask masking string
 * @return password
 */
  static String promptForPassword(Shell context, String message, Character mask) {
    boolean reEnableHistory = false
    if (context.getReader()?.getUseHistory()) {
      context.getReader().setUseHistory(false)
      reEnableHistory = true;
    }

    if (context.isConsole()) {
      def savedPrompt = context.getReader().getDefaultPrompt()
      try {
        return context.getReader().readLine(renderMessage(message + " "), mask)
      } finally {
        context.getReader().setDefaultPrompt(savedPrompt)
        if (reEnableHistory) {
          context.getReader().setUseHistory(true)
        }
      }
    }
    println "$message " + getMessage(ENTER_VALUE_BELOW)
    return System.in.newReader().readLine()

  }

/**
 * Base method for reading lines from prompter
 * @param context command context
 * @param message question
 * @param defaultValue optional default value
 * @return answer
 */
  private static String readLine(Shell context, String message, def defaultValue = null) {
    boolean reEnableHistory = false
    if (context.getReader()?.getUseHistory()) {
      context.getReader().setUseHistory(false)
      reEnableHistory = true;
    }

    String msg = "$message " + (defaultValue ? "[$defaultValue] " : "")
    if (context.isConsole()) {
      def savedPrompt = context.getReader().getDefaultPrompt()
      try {
        // split message, print each line, otherwise backspace seems to cause indentation issues.
        def list = msg.readLines()
        int len = list.size()
        for (int i = 0; i < len - 1; i++) {
          context.getWriter().println(renderMessage(list.get(i)))
        }
        return context.getReader().readLine(renderMessage(list.get(len - 1))) ?: defaultValue
      } finally {
        context.getReader().setDefaultPrompt(savedPrompt)
        if (reEnableHistory) {
          context.getReader().setUseHistory(true)
        }
      }
    }
    println "$msg " + getMessage(ENTER_VALUE_BELOW)
    return System.in.newReader().readLine() ?: defaultValue
  }

  /**
   * Adapt values to their specification
   * this is only used for RESOURCE_TYPE for the moment (WON-7973)
   */
  public void adaptValues(Map<String, Object> values) {
    if (null == values) {
      return
    }
    for (key in values.keySet()) {
      if (!questionnaire.containsKey(key)) {
        continue
      }
      Map spec = questionnaire[key]
      if (spec[TYPE_KEY] == RESOURCE_TYPE) {
        def val = values[key]?.toString()
        values[key] = expandResourceReference(val)
      }
    }

  }

  public static String expandResourceReference(String source) {
    if (source.startsWith(ConfigLexicon.FILE_GROUP_CLASSPATH_PREFIX)) {
      return source
    } else {
      try {
        new URL(source)
      }
      catch (MalformedURLException e) {
        def file = new File(source)
        return file.getCanonicalPath()
      }
    }

  }

  private static String renderMessage(String message) {
    AnsiHelper.bold(message)
  }

  public static final PromptValidator CLASS_NAME_VALIDATOR = new RegexPromptValidator(CLASS_NAME_PATTERN)
  public static final PromptValidator ARTIFACT_ID_VALIDATOR = new RegexPromptValidator(MAVEN_ID_VALIDATOR)
  public static final PromptValidator HTTP_URL_VALIDATOR = new ResourcePromptValidator(false, null, true)
  public static final PromptValidator GROUP_ID_VALIDATOR = new RegexPromptValidator(MAVEN_ID_VALIDATOR)
  public static final PromptValidator VERSION_VALIDATOR = new RegexPromptValidator(MAVEN_ID_VALIDATOR)
  public static final PromptValidator PACKAGE_NAME_VALIDATOR = new RegexPromptValidator(PACKAGE_NAME_PATTERN)
  public static final PromptValidator PORT_VALIDATOR = new ClosurePromptValidator(
      { it ==~ /[0-9]+/ && Integer.parseInt(it) > 0 && Integer.parseInt(it) <= 65535 },
      getMessage(MUST_BE_VALID_PORT))
  public static final PromptValidator REST_PATH_VALIDATOR = new RestPathValidator()
  public static final PromptValidator PREFIX_VALIDATOR = new PrefixValidator()

}

/**
 * Interface for answer validators
 */
interface PromptValidator {
  /**
   * Validate a string
   * @param v string value to validate
   * @throws IllegalArgumentException if string is invalid
   */
  void validate(String v) throws IllegalArgumentException;

}

class NotEmptyPromptValidator implements PromptValidator {
  void validate(String s) throws IllegalArgumentException {
    if (s == null || s.isEmpty() || s.trim().isEmpty()) {
      throw new IllegalArgumentException(getMessage(ENTRY_CANNOT_BE_EMPTY))
    }
  }
}

class AggregatedPromptValidator implements PromptValidator {
  List<PromptValidator> validators

  AggregatedPromptValidator(List<PromptValidator> validators) {
    this.validators = validators
  }

  @Override
  void validate(String s) throws IllegalArgumentException {
    for (validator in validators) {
      validator.validate(s)
    }
  }
}

class ResourcePromptValidator implements PromptValidator {

  boolean checkOnline
  List<String> otherAcceptedValues
  boolean httpUrlsOnly

  ResourcePromptValidator(boolean checkOnline = false, List<String> otherAcceptedValues = null, boolean httpUrlsOnly = false) {
    this.checkOnline = checkOnline
    this.otherAcceptedValues = otherAcceptedValues
    this.httpUrlsOnly = httpUrlsOnly
  }

  @Override
  void validate(String s) throws IllegalArgumentException {
    if (otherAcceptedValues && s in otherAcceptedValues) {
      return
    }
    if (s == null || s.isEmpty()) {
      throw new IllegalArgumentException(getMessage(INVALID_ENTRY))
    }

    String v = s.trim()

    // Check for file
    if (!httpUrlsOnly && new File(v).exists()) {
      return
    }

    if (v.toLowerCase() ==~ /http[s]?:\/\/.*/) {

      if (checkOnline) {
        try {
          URL myURL = new URL(v);
          URLConnection myURLConnection = myURL.openConnection();
          myURLConnection.connect();
          return
        }
        catch (MalformedURLException e) {
          throw new IllegalArgumentException(getMessage(URL_INVALID, v))
        }
        catch (Exception e) {
          throw new IllegalArgumentException(getMessage(URL_NOT_ACCESSIBLE, v))
        }

      }
      return
    }
    throw new IllegalArgumentException(getMessage(NOT_A_RECOGNIZED_RESOURCE, v))
  }
}
/**
 * Validator accepting accepts that match a regex
 */
class RegexPromptValidator implements PromptValidator {

  final Pattern pattern

  RegexPromptValidator(Pattern pattern) {
    this.pattern = pattern
  }

  @Override
  void validate(String v) throws IllegalArgumentException {
    if (!(v ==~ pattern)) {
      throw new IllegalArgumentException(getMessage(VALUE_SHOULD_MATCH_PATTERN, v, pattern))
    }
  }
}

class BlackListPromptValidator implements PromptValidator {

  List<String> blacklist


  BlackListPromptValidator(List<String> blacklist) {
    this.blacklist = blacklist
  }

  @Override
  void validate(String v) throws IllegalArgumentException {
    if (blacklist.find { v.contains(it) }) {
      throw new IllegalArgumentException(getMessage(VALUE_SHOULD_NOT_BELONG_TO_LIST, v, blacklist))
    }
  }
}

/**
 * Validator that evaluate a closure on the value
 */
class ClosurePromptValidator implements PromptValidator {

  Closure closure
  String message


  ClosurePromptValidator(Closure closure, String invalidationMessage = null) {
    this.closure = closure
    this.message = invalidationMessage
  }

  @Override
  void validate(String v) throws IllegalArgumentException {
    if (!closure.call(v)) {
      if (message) throw new IllegalArgumentException("$message : $v")
      throw new IllegalArgumentException(CommonMessages.invalidValue(v))

    }
  }
}

class RestPathValidator implements PromptValidator {

  @Override
  void validate(String v) throws IllegalArgumentException {
    URI uri
    try {
      uri = new URI(v)
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage())
    }
    if (uri.getRawPath() != uri.toString()) {
      throw new IllegalArgumentException(getMessage(ILLEGAL_REST_PATH))
    }
  }
}

class PrefixValidator implements PromptValidator {

  @Override
  void validate(String v) throws IllegalArgumentException {
    if(v) {
      PromptHelper.CLASS_NAME_VALIDATOR.validate(v)
    }
  }
}








