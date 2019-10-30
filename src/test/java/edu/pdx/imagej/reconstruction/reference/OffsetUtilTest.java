/* Copyright (C) 2019 Portland State University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * For any questions regarding the license, please contact the Free Software
 * Foundation.  For any other questions regarding this program, please contact
 * David Cohoe at dcohoe@pdx.edu.
 */

package edu.pdx.imagej.reconstruction.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class OffsetUtilTest {
    @Test public void testBaseOffset()
    {
        assertEquals(-2, OffsetUtil.getOffset(-2, 4, 1, 5), "getOffset should work when no adjusting is involved.");
        assertEquals(-1, OffsetUtil.getOffset(-2, 3, 2, 5), "getOffset should work when the offset must be adjusted up because it became too low.");
        assertEquals(-4, OffsetUtil.getOffset(-2, 9, 2, 5), "getOffset should work when the offset must be adjusted down because it was always too high.");
        assertEquals(1, OffsetUtil.getOffset(2, 4, 2, 5), "getOffset should work when the offset must be adjusted down because it became too high.");
    }
    @Test public void testMultipleOffset()
    {
        assertEquals(-2, OffsetUtil.getMultiOffset(-2, 4, 1, 5, 1, 3), "getMultiOffset should work when no adjusting is involved.");
        assertEquals(-1, OffsetUtil.getMultiOffset(-2, 3, 2, 5, 1, 3), "getMultiOffset should work when the offset must be adjusted up because it got below minOut.");
        assertEquals(-3, OffsetUtil.getMultiOffset(-2, 4, 1, 5, 3, 5), "getMultiOffset should work when the offset must be adjusted down because maxIn + offset was too high.");
        assertEquals(-6, OffsetUtil.getMultiOffset(-2, 9, 1, 5, 1, 3), "getMultiOffset should work when the offset must be adjusted down because both minIn + offset and maxIn + offset was too high.");
    }
}
