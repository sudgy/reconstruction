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

import org.scijava.plugin.AbstractRichPlugin;

import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.filter.Filter;

/** Abstract base class to create {@link ReferencePlugin}s.
 */
public abstract class AbstractReferencePlugin
    extends AbstractRichPlugin
    implements ReferencePlugin
{
    /** {@inheritDoc}
     * <p>
     * It is redefined here as final to ensure that people won't be stupid and
     * try to override it anyway.
     */
    @Override
    final public void processFilteredField(ReconstructionField field, int t)
    {
        ReferencePlugin.super.processFilteredField(field, t);
    }
}
