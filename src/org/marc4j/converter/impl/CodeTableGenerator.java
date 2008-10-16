 // $Id$
/**
 * Copyright (C) 2002 Bas Peters
 *
 * This file is part of MARC4J
 *
 * MARC4J is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * MARC4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with MARC4J; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.marc4j.converter.impl;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * CodeTableGenerator.main
 * 
 * Invoked at build time to generate a java source file (named CodeTableGenerated.java) 
 * which when compiled will implement the CodeTableInterface (primarily through switch statements)
 * and which can be used be the AnselToUnicode converter instead of this class, and which will
 * produce the same results as the object CodeTable.
 * 
 * The following routines are only used in the code generation process, and are not available to
 * be called from within an application that uses Marc4j. 
 * 
 * @author Robert Haschart
 * @version $Revision$
 *  
 */
public class CodeTableGenerator extends CodeTable {

	public CodeTableGenerator(InputStream byteStream) 
	{
		super(byteStream);
	}

    public static void main(String args[])
    {
    	CodeTableGenerator ct = new CodeTableGenerator(CodeTable.class.getResourceAsStream("resources/codetables.xml"));
        ct.dumpTableAsSwitchStatement(System.out);
    }

    public void dumpTableAsSwitchStatement(PrintStream output)
    {
        output.println("package org.marc4j.converter.impl;");
        output.println("");
        output.println("// Warning: This file is generated by running the main routine in the file CodeTable.java ");
        output.println("// Warning: Do not edit this file, or all edits will be lost at the next build. ");
        output.println("public class CodeTableGenerated implements CodeTableInterface {");
        output.println("\tpublic boolean isCombining(int i, int g0, int g1) {");
        output.println("\t\tswitch (i <= 0x7E ? g0 : g1) {");
        Object combiningKeys[] = combining.keySet().toArray();
        Arrays.sort(combiningKeys);
        for (int combiningSel = 0; combiningSel < combiningKeys.length; combiningSel++)
        {
            Integer nextKey = (Integer)combiningKeys[combiningSel];
            output.println("\t\t\tcase 0x"+Integer.toHexString(nextKey)+":");
            Vector v = (Vector) combining.get(nextKey);
            Iterator vIter = v.iterator();
            if (vIter.hasNext())
            {
                output.println("\t\t\t\tswitch(i) {");
                while (vIter.hasNext())
                {
                    Integer vVal = (Integer)vIter.next();
                    output.println("\t\t\t\t\tcase 0x"+Integer.toHexString(vVal)+":");
                }
                output.println("\t\t\t\t\t\treturn(true);");
                output.println("\t\t\t\t\tdefault:");
                output.println("\t\t\t\t\t\treturn(false);");
                output.println("\t\t\t\t}");
            }
            else
            {
                output.println("\t\t\t\treturn(false);");
            }
        }
        output.println("\t\t\tdefault:");
        output.println("\t\t\t\treturn(false);");
        output.println("\t\t\t}");
        output.println("\t}");
        output.println("");
        output.println("\tpublic char getChar(int c, int mode) {");
        output.println("\t\tint code = getCharCode(c, mode);");
        output.println("\t\tif (code == -1) return((char)0);");
        output.println("\t\tif (code != 0) return((char)code);");
        output.println("\t\tcode = getCharCode(c < 0x80 ? c + 0x80 : c - 0x80 , mode);");
        output.println("\t\treturn((char)code);");
        output.println("\t}");
        output.println("");
        output.println("\tprivate int getCharCode(int c, int mode) {");
        output.println("\t\tif (c == 0x20) return  c;");
        output.println("\t\tswitch (mode) {");
        Object charsetsKeys[] = charsets.keySet().toArray();
        Arrays.sort(charsetsKeys);
        for (int charsetSel = 0; charsetSel < charsetsKeys.length; charsetSel++)
        {
            Integer nextKey = (Integer)charsetsKeys[charsetSel];
            output.println("\t\t\tcase 0x"+Integer.toHexString(nextKey)+":");
            if (nextKey.intValue() == 0x31)
            {
                output.println("\t\t\t\treturn(getMultiByteChar(c));");
            }
            else
            {
                HashMap map = (HashMap) charsets.get(nextKey);
                Object keyArray[] = map.keySet().toArray();
                Arrays.sort(keyArray);
                output.println("\t\t\t\tswitch(c) {");
                for (int sel = 0; sel < keyArray.length; sel++)
                {
                    Integer mKey = (Integer)keyArray[sel];
                    Character c = (Character)map.get(mKey);
                    if (c != null)
                        output.println("\t\t\t\t\tcase 0x"+Integer.toHexString(mKey)+":  return(0x"+Integer.toHexString((int)c.charValue())+"); ");
                    else
                        output.println("\t\t\t\t\tcase 0x"+Integer.toHexString(mKey)+":  return(0); ");
                }
                output.println("\t\t\t\t\tdefault:  return(0);");
                output.println("\t\t\t\t}");
            }
        }
        output.println("\t\t\tdefault: return(-1);  // unknown charset specified ");
        output.println("\t\t}");
        output.println("\t}");
        output.println("");
        StringBuffer getMultiByteFunc = new StringBuffer();
        getMultiByteFunc.append("\tpublic int getMultiByteChar(int c) {\n");
                
        HashMap map = (HashMap) charsets.get(new Integer(0x31));
        Object keyArray[] = map.keySet().toArray();
        Arrays.sort(keyArray);
        
        // Note the switch statements generated for converting multibyte characters must be 
        // divided up like this so that the 64K code size per method limitation is not exceeded. 
        
        dumpPartialMultiByteTable(output, getMultiByteFunc, keyArray, map, 0x210000, 0x214fff);
        dumpPartialMultiByteTable(output, getMultiByteFunc, keyArray, map, 0x215000, 0x21ffff);
        dumpPartialMultiByteTable(output, getMultiByteFunc, keyArray, map, 0x220000, 0x22ffff);
        dumpPartialMultiByteTable(output, getMultiByteFunc, keyArray, map, 0x230000, 0x27ffff);
        dumpPartialMultiByteTable(output, getMultiByteFunc, keyArray, map, 0x280000, 0x7f7fff);
        
        getMultiByteFunc.append("\t\treturn(0);\n");
        getMultiByteFunc.append("\t}");
        output.println(getMultiByteFunc.toString());
        
        output.println("}");

    }

    public void dumpPartialMultiByteTable(PrintStream output, StringBuffer buffer, Object keyArray[], HashMap map, int startByte, int endByte)
    {
        String startByteStr = "0x"+Integer.toHexString(startByte);
        String endByteStr = "0x"+Integer.toHexString(endByte);
        buffer.append("\t\tif (c >= "+startByteStr+" && c <= "+endByteStr+")  return (getMultiByteChar_"+startByteStr+"_"+endByteStr+"(c));\n");
        
        output.println("\tpublic char getMultiByteChar_"+startByteStr+"_"+endByteStr+"(int c) {");
        output.println("\t\tswitch(c) {");
        for (int sel = 0; sel < keyArray.length; sel++)
        {
            Integer mKey = (Integer)keyArray[sel];
            Character c = (Character)map.get(mKey);
            if (mKey >= startByte && mKey <= endByte)
            {
                if (c != null)
                    output.println("\t\t\tcase 0x"+Integer.toHexString(mKey)+":  return((char)0x"+Integer.toHexString((int)c.charValue())+"); ");
                else
                    output.println("\t\t\tcase 0x"+Integer.toHexString(mKey)+":  return((char)0); ");
            }
        }
        output.println("\t\t\tdefault: return((char)0);");
        output.println("\t\t}");
        output.println("\t}");  
        output.println("");  
    }
    

}
