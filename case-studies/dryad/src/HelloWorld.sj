.import ext/test/PrintExt
.import ext/CommentExt
.bytecode 50.0
.class public HelloWorld
.super java/lang/Object
 
.method public <init>()V
  .limit stack 1
  .limit locals 1 
  aload_0
  invokespecial java/lang/Object/<init>()V
  return
.end method 

.comment
.method public <init>()V
  .limit stack 1
  .limit locals 1 
  aload_0
  invokespecial java/lang/Object/<init>()V
  return
.end method 
.end comment
; Kommentar
  
.method public static main([Ljava/lang/String;)V
  .limit stack 2
  .limit locals 1
  .print "Hello World"
  .print "my String"
  return
.end method