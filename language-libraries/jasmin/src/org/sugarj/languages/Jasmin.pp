[
   NoArgs                      -- KW[""],
   Void                        -- KW["V"],
   JVMResultType               -- _1,
   JVMResultType.1:alt         -- _1 _2,
   JVMMethodArgsType           -- H hs=0[KW["("] _1 KW[")"]],
   JVMMethodArgsType.1:iter    -- _1,
   ALOAD0                      -- KW["aload_0"],
   RETURN                      -- KW["return"],
   LDC                         -- KW["ldc"] H hs=0[KW["\""] _1 KW["\""]],
   INVOKESPECIAL               -- KW["invokespecial"] H hs=0[_1 _2 _3],
   GETSTATIC                   -- KW["getstatic"] _1 _2,
   INVOKEVIRTUAL               -- KW["invokevirtual"] H hs=0[_1 _2 _3],
   JasminMethod                -- H[KW[".method"] _1 H hs=0[_2 _3 _4]] _5 KW[".end method"],
   JasminMethod.5:iter-star    -- _1,
   StackLimit                  -- H[KW[".limit stack"] _1],
   LocalsLimit                 -- H[KW[".limit locals"] _1],
   Instruction                 -- _1,
   LabeledInstruction          -- H[H hs=0[_1 KW[":"]] _2],
   LineNumber                  -- H[KW[".line"] _1],
   FieldAttribute              -- _1,
   FieldAttribute .1:alt       -- KW[".deprecated"] _1,
   FieldAttribute .1:alt.1:seq -- KW[".signature"] _1,
   FieldValue                  -- KW["="] _1,
   FieldValue                  -- KW["="] _1,
   JasminField                 -- KW[".field"] _1 _2 _3 _4 _5,
   JasminField.1:iter-star     -- _1,
   JasminField.4:opt           -- _1,
   JasminField.5:opt           -- _1,
   JasminField                 -- KW[".field"] _1 _2 _3 _4 _5 _6 KW[".end field"],
   JasminField.1:iter-star     -- _1,
   JasminField.4:opt           -- _1,
   JasminField.5:opt           -- _1,
   JasminField.6:iter-star     -- _1,
   AccessFlags                 -- _1,
   AccessFlags.1:iter-star     -- _1,
   Public                      -- KW["public"],
   Private                     -- KW["private"],
   Protected                   -- KW["protected"],
   Static                      -- KW["static"],
   Final                       -- KW["final"],
   Synchronized                -- KW["synchronized"],
   Super                       -- KW["super"],
   Bridge                      -- KW["bridge"],
   Volatile                    -- KW["volatile"],
   Varargs                     -- KW["varargs"],
   Transient                   -- KW["transient"],
   Native                      -- KW["native"],
   Interface                   -- KW["interface"],
   Abstract                    -- KW["abstract"],
   Strict                      -- KW["strict"],
   Synthetic                   -- KW["synthetic"],
   Annotation                  -- KW["annotation"],
   Enum                        -- KW["enum"],
   Static                      -- KW["static"],
   BytecodeVersion             -- H[KW[".bytecode"] H hs=0[_1 KW["."] _2]],
   SourceReference             -- H[KW[".source"] _1],
   DebugDirective              -- KW[".debug"] _1,
   Signature                   -- KW[".signature"] _1,
   ClassSpec                   -- H[KW[".class"] _1 _2],
   SuperSpec                   -- H[KW[".super"] _1],
   Implements                  -- KW[".implements"] _1,
   SignatureField              -- KW[".signature"] _1,
   EnclosingMethod             -- KW[".enclosing method"] _1 KW["("] _2 KW[")"] _3,
   EnclosingMethod.2:iter-star -- _1,
   EnclosingMethod.3:opt       -- _1,
   JasminHeader                -- _1 _2 _3 _4 _5 _6 _7 _8,
   JasminHeader.1:opt          -- _1,
   JasminHeader.2:opt          -- _1,
   JasminHeader.5:iter-star    -- _1,
   JasminHeader.6:opt          -- _1,
   JasminHeader.7:opt          -- _1,
   JasminHeader.8:iter-star    -- _1,
   JasminUnit                  -- V[_1 _2 _3],
   JasminUnit.2:iter-star      -- _1,
   JasminUnit.3:iter-star      -- _1
]