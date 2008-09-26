// $Id$
/**
 * Copyright (C) 2004 Bas Peters
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
 *
 */

package org.marc4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import org.marc4j.MarcPermissiveStreamReader;
import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

/**
 * 
 * @author Robert Haschart
 * @version $Id$
 *
 */
public class MarcDirStreamReader implements MarcReader
{
    File list[];
    MarcReader curFileReader;
    int curFileNum;
    boolean permissive;
    boolean convertToUTF8;
    String defaultEncoding;
    
    public MarcDirStreamReader(String dirName)
    {
        File dir = new File(dirName);
        init(dir, false, false, null);
    }
    
    public MarcDirStreamReader(File dir)
    {
        init(dir, false, false, null);
    }

    public MarcDirStreamReader(String dirName, boolean permissive, boolean convertToUTF8)
    {
        File dir = new File(dirName);
        init(dir, permissive, convertToUTF8, null);
    }
    
    public MarcDirStreamReader(File dir, boolean permissive, boolean convertToUTF8)
    {
        init(dir, permissive, convertToUTF8, null);
    }

    public MarcDirStreamReader(String dirName, boolean permissive, boolean convertToUTF8, String defaultEncoding)
    {
        File dir = new File(dirName);
        init(dir, permissive, convertToUTF8, defaultEncoding);
    }
    
    public MarcDirStreamReader(File dir, boolean permissive, boolean convertToUTF8, String defaultEncoding)
    {
        init(dir, permissive, convertToUTF8, defaultEncoding);
    }

    private void init(File dir, boolean permissive, boolean convertToUTF8, String defaultEncoding)
    {
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return(name.endsWith("mrc"));
            }
        };
        this.permissive = permissive;
        this.convertToUTF8 = convertToUTF8;
        list = dir.listFiles(filter);
        java.util.Arrays.sort(list);
        curFileNum = 0;
        curFileReader = null;
        this.defaultEncoding = defaultEncoding;
    }
    
    public boolean hasNext()
    {
        if (curFileReader == null || curFileReader.hasNext() == false)
        {
            nextFile();
        }
        return (curFileReader == null ? false : curFileReader.hasNext());
    }

    private void nextFile()
    {
        if (curFileNum != list.length)
        {
            try
            {
                System.err.println("Switching to input file: "+ list[curFileNum]);
                if (defaultEncoding != null)
                {
                    curFileReader = new MarcPermissiveStreamReader(new FileInputStream(list[curFileNum++]), permissive, convertToUTF8, defaultEncoding);
                }
                else
                {
                    curFileReader = new MarcPermissiveStreamReader(new FileInputStream(list[curFileNum++]), permissive, convertToUTF8);
                }
            }
            catch (FileNotFoundException e)
            {
                nextFile();
            }
        }
        else 
        {
            curFileReader = null;
        }
    }

    public Record next()
    {
        if (curFileReader == null || curFileReader.hasNext() == false)
        {
            nextFile();
        }
        return (curFileReader == null ? null : curFileReader.next());
    }

}
