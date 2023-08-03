import os

print("Hello 20")
user=os.environ.get("REQUEST_user")
uuid=os.environ.get("REQUEST_uuid")
auth=os.environ.get("REQUEST_authorization")
print(user)
print(uuid)
print(auth)
#for key,value in os.environ.items():
 #   print(key,value)