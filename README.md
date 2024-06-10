# AES Encryption Algorithm Implementation

This project provides a Java implementation of a simplified AES (Advanced Encryption Standard) encryption algorithm. AES
is a symmetric key encryption algorithm widely used for securing sensitive data.

## Overview

The project consists of several classes:

- **AES**: Implements the AES encryption algorithm, including methods for encryption and decryption, as well as
  definitions for S-box, inverse S-box, mix matrix, and inverse mix matrix.
- **Block**: Represents a block of binary data and provides methods for various operations like XOR, left shift, modular
  multiplication, and segment extraction.
- **Key**: Represents a cryptographic key for AES encryption, supporting key expansion and sub-key generation.
- **SBox**: Represents an S-box (Substitution-box) used in AES encryption, providing a method for applying the S-box
  transformation to a block.
- **State**: Represents the state in the AES encryption process, providing methods for creating, manipulating, and
  converting the state.

## Usage

To use the AES encryption algorithm:

1. Create an instance of the `AES` class with a key represented by a `Block`.
2. Use the `cipher` method to encrypt plain text and the `decipher` method to decrypt cipher text.

Example:

```java
String plain="0110111101101011";
String key="0010110101010101";

Block plainBlock=new Block(plain);
Block keyBlock=new Block(key);

AES aes=new AES(keyBlock);

Block cipherBlock=aes.cipher(plainBlock);
Block decipherBlock=aes.decipher(cipherBlock);

System.out.println("Plain: "+plainBlock);
System.out.println("Cipher: "+cipherBlock);
System.out.println("Decipher: "+decipherBlock);
```

## Contributors

- **Ricardo BOKA** - [ricardo.boka@dauphine.eu](mailto:ricardo.boka@dauphine.eu),
- **Sébastien GIRET-IHMAUS** - [sebastien.giret-ihmaus@dauphine.eu](mailto:sebastien.giret-ihmaus@dauphine.eu),
- **Nelson PROIA** - [nelson.proia@dauphine.eu](mailto:nelson.proia@dauphine.eu),
- **Mathieu ANDRIN** - [mathieu.andrin@dauphine.eu](mailto:mathieu.andrin@dauphine.eu).

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
