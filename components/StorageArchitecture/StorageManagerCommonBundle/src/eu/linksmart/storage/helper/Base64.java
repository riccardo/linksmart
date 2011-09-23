/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
/**
 * Copyright (C) 2006-2010 [University of Paderborn]
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.linksmart.storage.helper;

/* Copyright (c) 2002,2003, Stefan Haustein, Oberhausen, Rhld., Germany
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or
* sell copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The  above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
* IN THE SOFTWARE. */

import java.io.*;

public class Base64 {

   static final char[] charTab =
       "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
           .toCharArray();

   public static String encode(byte[] data) {
       return encode(data, 0, data.length, null).toString();
   }

   /** Encodes the part of the given byte array denoted by start and
   len to the Base64 format.  The encoded data is appended to the
   given StringBuffer. If no StringBuffer is given, a new one is
   created automatically. The StringBuffer is the return value of
   this method. */

   public static StringBuffer encode(
       byte[] data,
       int start,
       int len,
       StringBuffer buf) {

       if (buf == null)
           buf = new StringBuffer(data.length * 3 / 2);

       int end = len - 3;
       int i = start;
       int n = 0;

       while (i <= end) {
           int d =
               ((((int) data[i]) & 0x0ff) << 16)
                   | ((((int) data[i + 1]) & 0x0ff) << 8)
                   | (((int) data[i + 2]) & 0x0ff);

           buf.append(charTab[(d >> 18) & 63]);
           buf.append(charTab[(d >> 12) & 63]);
           buf.append(charTab[(d >> 6) & 63]);
           buf.append(charTab[d & 63]);

           i += 3;

           if (n++ >= 14) {
               n = 0;
               buf.append("\r\n");
           }
       }

       if (i == start + len - 2) {
           int d =
               ((((int) data[i]) & 0x0ff) << 16)
                   | ((((int) data[i + 1]) & 255) << 8);

           buf.append(charTab[(d >> 18) & 63]);
           buf.append(charTab[(d >> 12) & 63]);
           buf.append(charTab[(d >> 6) & 63]);
           buf.append("=");
       }
       else if (i == start + len - 1) {
           int d = (((int) data[i]) & 0x0ff) << 16;

           buf.append(charTab[(d >> 18) & 63]);
           buf.append(charTab[(d >> 12) & 63]);
           buf.append("==");
       }

       return buf;
   }

   static int decode(char c) {

       if (c >= 'A' && c <= 'Z')
           return ((int) c) - 65;
       else if (c >= 'a' && c <= 'z')
           return ((int) c) - 97 + 26;
       else if (c >= '0' && c <= '9')
           return ((int) c) - 48 + 26 + 26;
       else
           switch (c) {
               case '+' :
                   return 62;
               case '/' :
                   return 63;
               case '=' :
                   return 0;
               default :
                   throw new RuntimeException(
                       "unexpected code: " + c);
           }
   }

   /** Decodes the given Base64 encoded String to a new byte array.
   The byte array holding the decoded data is returned. */

   public static byte[] decode(String s) {

       ByteArrayOutputStream bos = new ByteArrayOutputStream();
       try {

           decode(s, bos);

       }
       catch (IOException e) {
           throw new RuntimeException();
       }
       return bos.toByteArray();
   }

   public static void decode(String s, OutputStream os)
       throws IOException {
       int i = 0;

       int len = s.length();

       while (true) {
           while (i < len && s.charAt(i) <= ' ')
               i++;

           if (i == len)
               break;

           int tri =
               (decode(s.charAt(i)) << 18)
                   + (decode(s.charAt(i + 1)) << 12)
                   + (decode(s.charAt(i + 2)) << 6)
                   + (decode(s.charAt(i + 3)));


           os.write((tri >> 16) & 255);
           if (s.charAt(i + 2) == '=')
               break;
           os.write((tri >> 8) & 255);
           if (s.charAt(i + 3) == '=')
               break;
           os.write(tri & 255);

           i += 4;
       }
   }
}
