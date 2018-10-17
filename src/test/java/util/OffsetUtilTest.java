/* Copyright (C) 2018 Portland State University
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

package edu.pdx.imagej.reconstruction.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class OffsetUtilTest {
    @Test public void test_base_offset()
    {
        assertEquals("get_offset should work when no adjusting is involved.", -2, OffsetUtil.get_offset(-2, 4, 1, 5));
        assertEquals("get_offset should work when the offset must be adjusted up because it became too low.", -1, OffsetUtil.get_offset(-2, 3, 2, 5));
        assertEquals("get_offset should work when the offset must be adjusted down because it was always too high.", -4, OffsetUtil.get_offset(-2, 9, 2, 5));
        assertEquals("get_offset should work when the offset must be adjusted down because it became too high.", 1, OffsetUtil.get_offset(2, 4, 2, 5));
    }
    @Test public void test_multiple_offset()
    {
        assertEquals("get_multi_offset should work when no adjusting is involved.", -2, OffsetUtil.get_multi_offset(-2, 4, 1, 5, 1, 3));
        assertEquals("get_multi_offset should work when the offset must be adjusted up because it got below min_out.", -1, OffsetUtil.get_multi_offset(-2, 3, 2, 5, 1, 3));
        assertEquals("get_multi_offset should work when the offset must be adjusted down because max_in + offset was too high.", -3, OffsetUtil.get_multi_offset(-2, 4, 1, 5, 3, 5));
        assertEquals("get_multi_offset should work when the offset must be adjusted down because both min_in + offset and max_in + offset was too high.", -6, OffsetUtil.get_multi_offset(-2, 9, 1, 5, 1, 3));
    }
}
