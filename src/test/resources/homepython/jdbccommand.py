import os, sys, json
import jaydebeapi


def getfiles():
    return (os.environ["TMPFILE"], os.environ["UPLOADEDFILE"])


def getconn():
    url = os.environ["ENV_url"]
    user = os.environ["ENV_user"]
    password = os.environ["ENV_password"]
    driverclass = os.environ["ENV_driverclass"]
    jdbcjar = os.environ["ENV_jdbcjar"]

    #    return jaydebeapi.connect("com.ibm.db2.jcc.DB2Driver","jdbc:db2://ubun:50000/sample",["db2inst1","secret123"],"/opt/ibm/db2/V11.5/java/db2jcc4.jar")
    return jaydebeapi.connect(driverclass, url, [user, password], jdbcjar)


def getjson():
    (t, u) = getfiles()
    with open(u) as f:
        return json.load(f)


def writeok():
    (t, u) = getfiles()
    with open(t, "w+") as f:
        f.write("{ 'res' : true } ")


def add():
    print("Rest add method")
    url = os.environ["ENV_url"]
    print(url)
    data = getjson()
    id = data["id"]
    name = data["name"]
    print(str(id) + " " + name)
    print(data)
    writeok()
    with getconn() as conn:
        with conn.cursor() as curs:
            curs.execute("insert into resttest values (?, ?)", (id, name))


def change():
    print("Rest change method")
    data = getjson()
    id = data["id"]
    name = data["name"]
    print(str(id) + " " + name)
    print(data)
    writeok()
    with getconn() as conn:
        with conn.cursor() as curs:
            curs.execute("update resttest set name=? where id=?", (name, id))


if __name__ == '__main__':
    what = sys.argv[1]
    if what == "add": add()
    if what == "change": change()
