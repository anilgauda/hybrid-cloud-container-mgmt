hybrid-cloud-container-mgmt
==

# Encryption (DRAFT??)

All private key files are stored in home directory defined in application.proprties
These files are encrypted using AES encryption with the secret key(password) which is not
stored in the server. Every time an user makes a deployment he will be asked for the password
which will be used to decrypt the stored private keys.

In case an attacker hacks into the server, he will still not have access to the secret key hence
the attack can be delayed until any kind of brute force attack takes place.



TODO

 - Add container_id in container_deployments er diagram
 - Add extra fields from VM.java