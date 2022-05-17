import os

tm = os.environ["TMPFILE"]

f = open(tm, "w+")
f.write("{ 'res' : [{ 'a' : 'b'} ]}")
f.close()
