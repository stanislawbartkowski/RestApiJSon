import os

tm = os.environ["TMPFILE"]
params = os.environ["PARAMS"]

f = open(tm, "w+")
f.write("{'response' : '" + params + "'}")
f.close()
