package com.caesar;

import java.io.*;
import java.util.Arrays;

/**
 * Шифр Цезаря
 */
public class Caesar {
    private final String cfgname = "caesar.cfg"; // файл настроек с алфавитом и значением сдвига
    private final String frqname = "freq.txt";   // файл с данными о частоте появления в тексте сомволов алфавита
    private final String charset = "UTF8";       // кодировка текстовых файлов
    private final int ratio = 10000;             // множитель для частоты появления символов

    private char[] alphabet;                     // криптографический алфавит
    private int shift;                           // значение сдвига для шифра Цезаря

    public Caesar() throws IOException {
        loadCfg();
    }

    /**
     * Загрузка настроек
     * alphabet = [алфавит]
     * shift = 5
     * @throws IOException
     */
    public void loadCfg() throws IOException{
        try(
            FileInputStream fis = new FileInputStream(cfgname);
            InputStreamReader isr = new InputStreamReader(fis, charset);
            BufferedReader br = new BufferedReader(isr);
        ) {
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // пропускаем пустые строки и строки начинающиеся с #
                if (line.length() == 0 || line.startsWith("#")) continue;
                int pos = line.indexOf('=');
                if (pos <= 0) continue;
                // определяем key = val
                String key = line.substring(0, pos).trim();
                String val = line.substring(pos + 1).trim();

                if (key.equals("alphabet")) {
                    // алфавит
                    if (val.length() > 2) {
                        // remove скобки []
                        alphabet = val.substring(1, val.length() - 1).toCharArray();
                    }
                } else if (key.equals("shift")) {
                    // значение сдвига
                    shift = Integer.parseInt(val);
                }
            }
            // ограничиваем сдвиг размером алфавита
            if (alphabet != null && alphabet.length > 0) shift = shift % alphabet.length;
        }
    }

    /**
     * Устанавливаем значение сдвига для шифра
     * @param shift
     */
    public void setShift(int shift) {
        this.shift = shift;
    }

    /**
     * Поиск номера символа в алфавите
     * @param c
     * @return
     * номер символа или -1 если символ не найден в алфавите
     */
    private int indexOf(char c){
        for(int i=0; i<alphabet.length; i++){
            if(alphabet[i]==c) return i;
        }
        return -1;
    }

    /**
     * Значение символа после шифрования
     * @param c
     * @param shft
     * @return
     */
    private char shiftRight(char c, int shft){
        int idx = indexOf(c);
        if(idx<0) return c;
        idx = (idx + shft) % alphabet.length;
        return alphabet[idx];
    }

    /**
     * Значение символа после расшифрования
     * @param c
     * @param shft
     * @return
     */
    private char shiftLeft(char c, int shft){
        int idx = indexOf(c);
        if(idx<0) return c;
        idx = (alphabet.length + idx - shft) % alphabet.length;
        return alphabet[idx];
    }

    /**
     * Шифрование строки текста с заданным сдвигом
     * @param text
     * @param shft
     * @return
     */
    public String encrypt(String text, int shft){
        if(text==null || text.length()==0) return text;
        char[] res = new char[text.length()];
        char[] src = text.toCharArray();
        for(int i=0; i< src.length; i++){
            res[i] = shiftRight(src[i],shft);
        }
        return String.valueOf(res);
    }

    /**
     * Расшифрование строки текста с заданным сдвигом
     * @param text
     * @param shft
     * @return
     */
    public String decrypt(String text, int shft){
        if(text==null || text.length()==0) return text;
        char[] res = new char[text.length()];
        char[] src = text.toCharArray();
        for(int i=0; i< src.length; i++){
            res[i] = shiftLeft(src[i],shft);
        }
        return String.valueOf(res);
    }

    /**
     * Шифрование файла с заданным сдвигом
     * @param name - имя файла для шифрования
     * @param out  - имя файла для результата шифрования
     * @return
     * @throws IOException
     */
    public int encryptFile(String name, String out) throws IOException{
        int len = 0;
        try(FileInputStream fis = new FileInputStream(name);
            InputStreamReader isr = new InputStreamReader(fis, charset);
            FileOutputStream fou = new FileOutputStream(out);){
            char buf[] = new char[1024];
            int size = 0;
            
            while( (size = isr.read(buf))>=0 ){
                if(size==0) continue;
                String src = new String(buf,0,size);
                len+=src.length();
                String enc = encrypt(src,shift);
                fou.write(enc.getBytes(charset));
            }
        }catch(IOException e){
            e.printStackTrace();
            throw e;
        }
        return len;
    }

    /**
     * Расшифрование файла с заданным сдвигом
     * @param name - имя файла для расшифрования
     * @param out  - имя файла для результата расшифрования
     * @return
     * @throws IOException
     */
    public int decryptFile(String name, String out) throws IOException{
        int len = 0;
        try(FileInputStream fis = new FileInputStream(name);
            InputStreamReader isr = new InputStreamReader(fis, charset);
            FileOutputStream fou = new FileOutputStream(out);){
            char buf[] = new char[1024];
            int size = 0;
            
            while( (size = isr.read(buf))>=0 ){
                if(size==0) continue;
                String src = new String(buf,0,size);
                len+=src.length();
                String enc = decrypt(src,shift);
                fou.write(enc.getBytes(charset));
            }
        }catch(IOException e){
            e.printStackTrace();
            throw e;
        }
        return len;
    }

    /**
     * Анализ частоты использования символов в тексте
     * @param name - имя файла для анализа
     * @return int[] - массив с частотой употребления символов
     * @throws IOException
     */
    public int[] analizeFile(String name) throws IOException{
        int len = 0;
        int[] freq = new int[alphabet.length];
        Arrays.fill(freq, 0);

        try(FileInputStream fis = new FileInputStream(name);
            InputStreamReader isr = new InputStreamReader(fis, charset);
        ){
            char buf[] = new char[1024];
            int size = 0;

            while( (size = isr.read(buf))>=0 ){
                if(size==0) continue;
                len+=size;
                for(int i=0; i<size; i++) {
                    int idx = indexOf(buf[i]);
                    if(idx<0) continue;
                    freq[idx]++;
                }
            }
        }catch(IOException e){
            e.printStackTrace();
            throw e;
        }
        int sum = 0;
        for(int i=0; i<freq.length; i++){
            freq[i] = freq[i]*ratio / len;
            sum+=freq[i];
        }
        return freq;
    }

    /**
     * Запись частоты употребления символов в файл
     * @param freq
     * @throws IOException
     */
    public void writeFreq(int[] freq) throws IOException{
        try(
                FileOutputStream fos = new FileOutputStream(frqname);
                OutputStreamWriter osw = new OutputStreamWriter(fos,charset);
                ){
            for(int i=0; i<freq.length && i<alphabet.length; i++){
                osw.write(alphabet[i]);
                osw.write('=');
                osw.write(String.valueOf(freq[i]));
                osw.write('\n');
            }
            osw.flush();
        }catch(IOException e){
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Считывание частот употребления символов из файла
     * @return
     * @throws IOException
     */
    public int[] readFreq() throws IOException{
        int[] res = new int[alphabet.length];
        Arrays.fill(res,0);
        try(
                FileInputStream fis = new FileInputStream(frqname);
                InputStreamReader isr = new InputStreamReader(fis,charset);
                BufferedReader br = new BufferedReader(isr);
                ){
            String line = null;
            while((line=br.readLine())!=null){
                if(line.length()<3) continue;
                char c = line.charAt(0);
                int pos = line.indexOf('=');
                if(pos<1) continue;
                String str = line.substring(pos+1).trim();
                int val = Integer.parseInt(str);
                int idx = indexOf(c);
                if(idx<0) continue;
                res[idx] = val;
            }
        }
        return res;
    }

    /**
     * Подсчет количиства употребления символа "пробел"
     * @param str
     * @return
     */
    public int countSpace(String str){
        if(str==null || str.length()==0) return 0;
        int count = 0;
        for(int i=0; i< str.length(); i++){
            if(str.charAt(i)==' ') count++;
        }
        return count;
    }

    /**
     * Подбор величины сдвиго по частоте употребления символа "пробел"
     * @param name - файла для анализа
     * @return вероятная величина сдвига
     * @throws IOException
     */
    public int bruteforce(String name) throws IOException{
        int[] freq = readFreq();
        int spaceFreq = freq[indexOf(' ')]; // per 10000
        FileInputStream fis = new FileInputStream(name);
        InputStreamReader isr = new InputStreamReader(fis,charset);
        // для анализа используем блок размером до 1024*1024 символов
        char[] buf = new char[1024*1024]; // 1 MB
        int size = isr.read(buf);

        String text = new String(buf,0,size);
        int matchedShift = 0; // меличина наиболее похожего сдвига
        int minDist = ratio;  // минимальная разница частот между шаблонным символом "пробел" и в расшифрованном блоке
        for(int shft=1; shft<alphabet.length; shft++){
            String dec = decrypt(text, shft);
            int spaces = countSpace(dec);
            int frq = spaces * ratio / size;
            int dist = Math.abs(spaceFreq-frq);
            if(minDist>dist) {
                matchedShift = shft;
                minDist = dist;
            }
        }
        System.out.println("matchedShift = "+matchedShift+", minDist = "+minDist);
        return  matchedShift;
    }


    public static void main(String[] args) throws Exception {
        Caesar caesar = new Caesar();

        int len = 0;

        if(args.length < 2) {
            System.out.println("Arguments: <encrypt|decrypt|analize|brute> <source file> [dest file]");
            return;
        }
        String fileIn  = args[1];

        if("analize".equalsIgnoreCase(args[0])){
            int[] freq = caesar.analizeFile(args[1]);
            caesar.writeFreq(freq);
            int[] res = caesar.readFreq();
            int sum = 0;
            for(int i=0; i<res.length; i++){
                System.out.println(""+ caesar.alphabet[i]+" "+res[i]);
                sum+=res[i];
            }
            System.out.println("sum = "+sum);
        } else if("brute".equalsIgnoreCase(args[0])) {
            int res = caesar.bruteforce(args[1]);
            if(res==0) {
                System.out.println("Can't find matched shift");
            } else {
                String fileOut = (args.length > 2) ? args[2] : (fileIn + "." + "dec");
                caesar.setShift(res);
                len = caesar.decryptFile(args[1], fileOut);
                System.out.println("decrypted: " + len);
            }
        } else {
            boolean modeEncrypt = "encrypt".equalsIgnoreCase(args[0]);
            String fileOut = (args.length > 2) ? args[2] : (fileIn + "." + ((modeEncrypt) ? "enc" : "dec"));
            if (modeEncrypt) {
                len = caesar.encryptFile(fileIn, fileOut);
                System.out.println("encrypted: " + len);
            } else {
                len = caesar.decryptFile(fileIn, fileOut);
                System.out.println("decrypted: " + len);
            }
        }
    }
}
