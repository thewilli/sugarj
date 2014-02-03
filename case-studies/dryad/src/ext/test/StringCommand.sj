.import org/sugarj/languages/Jasmin
.sugarmod ext/test/StringCommand
.sugar
	context-free syntax
		".test"	-> Instruction{cons("Test")}
	
	desugarings
		desugar-test
		
	rules
		desugar-test:
			Test -> "return"
.end sugar
