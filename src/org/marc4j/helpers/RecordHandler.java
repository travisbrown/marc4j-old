/**
 * Copyright (C) 2002 Bas Peters
 *
 * This file is part of MARC4J
 *
 * MARC4J is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * MARC4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MARC4J; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * As a special exception, if you link this library with other files to
 * produce an executable, this library does not by itself cause the
 * resulting executable to be covered by the GNU General Public License.
 * This exception does not however invalidate any other reasons why the
 * executable file might be covered by the GNU General Public License.
 */
package org.marc4j.helpers;

import org.marc4j.marc.Record;

/**
 * <p>Defines a set of Java callbacks to handle <code>Record</code> objects. </p>
 *
 * @author Bas Peters
 * @see Record
 */
public interface RecordHandler {

    /**
     * <p>Receives notification at the start of the collection.  </p>
     */
    public void startCollection();


    /**
     * <p>Receives notification when a record is parsed.  </p>
     *
     * @param record the {@link Record} object.
     */
    public void record(Record record);

    /**
     * <p>Receives notification at the end of the collection.</p>
     */
    public void endCollection();

}
