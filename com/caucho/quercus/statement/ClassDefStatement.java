/*
 * Copyright (c) 1998-2012 Caucho Technology -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.quercus.statement;

import com.caucho.quercus.Location;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.QuercusClass;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.program.InterpretedClassDef;
import com.caucho.util.L10N;

import edu.iastate.hungnv.empiricalstudy.EmpiricalStudy;

/**
 * Represents a class definition
 */
public class ClassDefStatement extends Statement {
  private final static L10N L = new L10N(ClassDefStatement.class);
  
  protected final InterpretedClassDef _cl;

  public ClassDefStatement(Location location, InterpretedClassDef cl)
  {
    super(location);

    _cl = cl;
  }

  @Override
  public Value execute(Env env)
  {
	  // EMPI ADDED BY HUNG
	  EmpiricalStudy.inst.statementExecuted(this, env);
	  // END OF ADDED CODE
	  
    env.addClass(_cl.getName(), _cl);

    return null;
  }

  public String toString()
  {
    return getClass().getSimpleName() + "[" + _cl + "]";
  }
}

