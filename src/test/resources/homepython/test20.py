import os

print("Hello 20");
user=os.environ.get("REQUEST_user")
uuid=os.environ.get("REQUEST_uuid")
print(user)
print(uuid)