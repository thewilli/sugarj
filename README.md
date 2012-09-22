SugarJ Eclipse plugin (recommended)
===================================

Visit the SugarJ web site http://sugarj.org

Installation
------------

1. Install Eclipse (follow instructions on eclipse.org).
2. Start Eclipse.
3. In Eclipse, select 'Install New Software' in the 'Help' menu.
4. In the 'work with' field, copy the SugarJ update site
   http://update.sugarj.org and hit enter.
5. Be sure to deselect the 'Group items by category' checkbox on the bottom of
   the window.
6. Select the latest instance of Sugarclipse and click continue. This will
   install the SugarJ compiler, Spoofax and the Sugarclipse plugin.


In addition, please ensure enough stack space (about 4-16 MB) is available for
the SDF parser. You can set the stack space of your Java runtime using the
-Xss16m command line argument when starting Eclipse or setting -Xss16m in your
eclipse.ini file.


Setting up a SugarJ project
---------------------------

1. Create a new Java project.
2. As for now, we need to register the SugarJ builder for this project by hand:
   Open your project's '.project' file in any text editor and replace the Java
   build command by the following code:
     <buildCommand>
       <name>org.sugarj.editor.SugarJBuilder</name>
       <arguments></arguments>
     </buildCommand> 
3. We're ready to go. Note: SugarJ source files must have the file extension
   ".sugj".



SugarJ standalone compiler
==========================


Installing SugarJ
-----------------

The SugarJ compiler is almost self-contained and only requires an installation
of a Java runtime version 6 or higher. Download `sugarj.zip` and
extract it to a location of your choice. The directory structure of
the archive is as follows. Adding `sugarj/bin` to the `PATH`
environmental variable of your platform will allow invocation of
SugarJ scripts without prefixing them by a location.

    sugarj/
      bin/              Scripts to invoke SugarJ
        sugarj          Compiler for *nix
        sugarj.bat      Compiler for Windows
        sugh            Alias of `sugarj -l haskell` on *nix
        sugh.bat        Alias of `sugarj -l haskell` on Windows
        sugj            Alias of `sugarj -l java` on *nix
        sugj.bat        Alias of `sugarj -l java` on Windows
      case-studies/     Sample SugarJ projects
      lib/              The back end
      README.md         This file


Invoking SugarJ
---------------

Suppose your working directory is `sugarj/`. You can invoke the
compiler like this:

    bin/sugarj -l java                       \
      --sourcepath case-studies/closures/src \
      -d           case-studies/closures/bin \
      concretesyntax/Test.sugj               # file(s) to compile relative
                                             # to sourcepath

The generated `Test.class` may be executed thus:

    java -cp case-studies/closures/bin concretesyntax.Test


Compiler options
----------------

    --atomic-imports         Parse all import statements simultaneously.
    --cache <arg>            Specifiy a directory for caching.
    --cache-info             show where files are cached
 -cp,--buildpath <arg>       Specify where to find compiled files.
                             Multiple paths can be given separated by ':'.
 -d <arg>                    Specify where to place compiled files
    --full-command-line      show all arguments to subprocesses
    --gen-files              Generate files?
    --help                   Print this synopsis of options
 -l,--language <arg>         Specify a language library to activate.
    --no-checking            Do not check resulting SDF and Stratego
                             files.
    --read-only-cache        Specify the cache to be read-only.
    --silent-execution       try to be silent
    --sourcepath <arg>       Specify where to find source files. Multiple
                             paths can be given separated by ':'.
    --sub-silent-execution   do not display output of subprocesses
 -v,--verbose                show verbose output
    --write-only-cache       Specify the cache to be write-only.
