.import org/sugarj/languages/Jasmin
.sugarmod ext/test/PrintExt
.sugar
	context-free syntax
		".print" String -> Instruction{cons("PrintCommand")}
	
	desugarings
		desugar-print
	rules
		desugar-print:
			PrintCommand(message) -> [GETSTATIC(JBCFieldRef(CRef("java/lang/System"),FRef("out"),JBCFieldDesc(Reference(CRef("java/io/PrintStream"))))),LDC(message),INVOKEVIRTUAL(JBCMethodRef(CRef("java/io/PrintStream"),MRef("println"),JBCMethodDesc([Reference(CRef("java/lang/String"))],Void)))]
.end sugar
