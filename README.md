# Cryptography
caesar
Работа с текстовыми файлами в кодировке UTF8
caesar.cfg - файл настроек (содержит алфавит и размер сдвига для шифра)
freq.txt - файл с частотами появления символов в тексте для bruteforce
example.txt - пример текста
text.txt - пример большого текста для расчета частот появления символов
brute.enc - пример зашифрованного текста для расшифровки bruteforce
brute.src - оригинальный текст
encrypt.bat - шифрование текста (например: encrypt.bat example.txt example.enc)
decrypt.bat - расшифрование текста (например: decrypt.bat example.enc example.dec)
analize.bat - анализ частот появления символов (например: analize.bat text.txt)
bruteforce.bat - расшифровка методом bruteforce (например: bruteforce.bat brute.enc brute.dec)
