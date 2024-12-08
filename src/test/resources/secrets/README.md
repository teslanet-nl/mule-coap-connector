# PSK secrets

## file format

Mule Coap Connector supports [Californiums PSK configuration file](https://github.com/eclipse-californium/californium/blob/main/scandium-core/src/main/java/org/eclipse/californium/scandium/dtls/pskstore/MultiPskFileStore.java). 
The file containes key-value pairs defining identities and associated key. The key format can either be bas64 encoded or hexadecimal.

## Ecrypting

An encrypted file containing preshared keys can be created using [Californium encryption utility](https://github.com/eclipse-californium/californium/tree/main/cf-utils/cf-encrypt).

E.g:

'''
java -jar cf-encrypt-3.12.1.jar --password64 `echo -n testPassword | base64` --encrypt --in pskfile.psk --out pskfile.aes
'''
