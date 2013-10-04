.sugarmod CommentExt
.sugar

	lexical syntax
		BlockComment -> LAYOUT
		
		BlockComment	-> CommentPart
		~[\.]			-> CommentPart
		PartA			-> CommentPart
		PartB			-> CommentPart
		PartC			-> CommentPart
		PartD			-> CommentPart
		PartE			-> CommentPart
		PartF			-> CommentPart
		PartG			-> CommentPart
		PartH			-> CommentPart
		PartI			-> CommentPart
		PartJ			-> CommentPart
		PartK			-> CommentPart
				
		[\.]		 	-> PartA
		".e"			-> PartB
		".en"			-> PartC
		".end"			-> PartD
		".end "			-> PartE
		".end c"		-> PartF
		".end co"		-> PartG
		".end com"		-> PartH
		".end comm"		-> PartI
		".end comme"	-> PartJ
		".end commen"	-> PartK
		
		".comment" CommentPart* ".end comment" -> BlockComment
		
	lexical restrictions
		PartA -/- [e]
		PartB -/- [n]
		PartC -/- [d]
		PartD -/- [\ ]
		PartE -/- [c]
		PartF -/- [o]
		PartG -/- [m]
		PartH -/- [m]
		PartI -/- [e]
		PartJ -/- [n]
		PartK -/- [t]

.end sugar