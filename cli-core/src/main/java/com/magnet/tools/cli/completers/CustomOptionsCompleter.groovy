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

package com.magnet.tools.cli.completers

import jline.Completor
import jline.NullCompletor
import jline.SimpleCompletor


/**
 * An aggregate completer that only
 * returns the matches for the first pattern
 * that matches.
 */
class CustomOptionsCompleter implements Completor {


  static public class OptionsCompleter {
    List<String> options;
    Completor completer;

    public OptionsCompleter(List<String> options, Completor completer) {
      this.options = options
      this.completer = completer
    }

  }

  OptionsCompleter[] optionCompleterList;
  List<String> aggregatedList;

  CustomOptionsCompleter(OptionsCompleter... optionCompleterList) {
    this.optionCompleterList = optionCompleterList;
    aggregatedList = new ArrayList<String>()
    for (OptionsCompleter optionCompleter: optionCompleterList) {
      aggregatedList.addAll(optionCompleter.options)
    }
  }

  @Override
  int complete(String buffer, int cursor, List candidates) {
    List<String> argumentList = Arrays.asList(buffer.split(" "));
    int i = argumentList.size();

    boolean isPreviousCompleted = buffer.endsWith(' ')
    boolean isLastOption = argumentList[i-1].startsWith("-")

    SimpleCompletor optionNamesCompleter = new SimpleCompletor(getRemainingOptions(isLastOption, isPreviousCompleted, argumentList) as String[])

    // if there is only one argument it is the command name.
    if (i==1) {
      if (!isPreviousCompleted) {
        candidates.add(" ")
        return cursor;
      }
      // add the candidates
      return optionNamesCompleter.complete("", 0,  candidates) == -1 ? -1 : cursor
    }

    String optionToFind;
    String currentCompletion;
    if (isLastOption) {

      optionToFind = argumentList[i-1]
      if (!isPreviousCompleted) {
        int retPos = cursor - optionToFind.length()
        return optionNamesCompleter.complete(optionToFind, 0,  candidates) == -1 ? -1 : retPos;
      }
      // we are starting a new option
      currentCompletion = ""
    } else if (isPreviousCompleted) {
      // add the candidates
      return optionNamesCompleter.complete("", 0,  candidates) == -1 ? -1 : cursor
    } else {
      if (argumentList[i-2].startsWith("-")) {
        // if we have already completed that argument and moved on..
        if (buffer.endsWith(" ")) {
          return optionNamesCompleter.complete("", 0,  candidates) == -1 ? -1 : cursor;
        }
        optionToFind = argumentList[i-2]
        currentCompletion = argumentList[i-1]
      } else {
        // complete the current option as if it were a
        currentCompletion = argumentList[i-1]
        return optionNamesCompleter.complete(currentCompletion, 0, candidates) == -1 ? -1 :
          cursor - currentCompletion.size();
      }
    }

    for (OptionsCompleter optionsCompleter: optionCompleterList) {
      if (optionsCompleter.options.contains(optionToFind)) {
        int retPos;
        if (currentCompletion.isEmpty()) {
          retPos = cursor + 1
        } else {
          retPos =  cursor - currentCompletion.size()
        }
        Completor theCompleter = optionsCompleter.completer;
        if (theCompleter instanceof NullCompletor) {
          return optionNamesCompleter.complete(currentCompletion, 0, candidates) ? -1 : retPos;
        }
        int completeRet = theCompleter.complete(currentCompletion, 0, candidates)
        return completeRet == -1 ? -1 : retPos + completeRet;
      }
    }
    currentCompletion = argumentList[i-1]
    return optionNamesCompleter.complete(currentCompletion, 0, candidates) ? -1 :
      cursor - currentCompletion.size()
  }

  private List<String> getRemainingOptions(boolean isLastOption, boolean isPreviousCompleted, List<String> argumentList) {
    return aggregatedList.minus(isLastOption && (!isPreviousCompleted || isPreviousCompleted && !toRemoveOption(argumentList.last())) ? argumentList.subList(0, argumentList.size() - 1) : argumentList)
  }

  /**
   * Option without value (NullCompletor) will be removed from candidates once it's added once
   * @param optionToFind
   * @return
   */
  private boolean toRemoveOption(String optionToFind) {
    for (OptionsCompleter optionsCompleter: optionCompleterList) {
      if (optionsCompleter.options.contains(optionToFind)) {
        return optionsCompleter.completer instanceof NullCompletor
      }
    }

    return false
  }
}
