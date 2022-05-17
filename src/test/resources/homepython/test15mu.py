import os

tm = os.environ["TMPFILE"]
tmc = os.environ["CONTENTFILE"]

f = open(tm, "w+")
f.write("{'response' : ' Hello '}")
f.close()

f = open(tmc, "w+")
f.write("<AAAA> Hello </AAAA>");
f.write("\n")
f.write("<NEXT> next </AAAA");
f.close()
