import os

tm = os.environ["TMPFILE"]

f = open(tm, "w+")
f.write("{}")
f.close()
