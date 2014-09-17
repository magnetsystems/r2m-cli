require "formula"

def error_no_java
  "ERROR: No Java executable found on PATH\n"
end

def error_incorrect_r2m(r2m_path)
"""
ERROR: r2m executables conflicting on PATH.
  It appears that you already have a r2m executable on your PATH: #{r2m_path} 
  Ensure that /usr/local/bin/r2m is in front of PATH to use the installed r2m.   
"""
end

def check_java
  
  java = which 'java'
  if not java 
    return error_no_java
  end
  
  nil
end

def check_r2m
 r2m = which 'r2m'
 if r2m 
   return error_incorrect_r2m(r2m)
 end
 nil
end

class JavaDependency < Requirement
  fatal true

  @error = nil

  def message
    @error
  end

  satisfy :build_env => false do
    @error = (check_java ? check_java : "")  
    not (@error != "") 
  end

end

#
# R2m formula (devel and stable)
#
class R2m < Formula
  homepage 'http://factory.magnet.com'
  url "https://github.com/magnetsystems/r2m-cli/releases/download/v0.9.1/r2m-installer-0.9.1.tar.gz"
  sha1 '5f5aa48698c2d64f9d6091834948830eee66ebc7'

  depends_on JavaDependency => :recommended

  
  def install
    prefix.install Dir['*']
  end


  def caveats 
    issues = (check_java ? check_java : "") + (check_r2m ? check_r2m : "")
    if (issues == "") 
      return """NO CAVEATS. INSTALLATION SUCCESSFUL!
  Congratulations! The R2M has been installed under /usr/local/bin/r2m.
  To uninstall it, run 'brew remove r2m'.
"""
    end 
    return issues
  end 


end
