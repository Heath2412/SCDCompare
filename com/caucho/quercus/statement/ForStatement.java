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
import com.caucho.quercus.env.BreakValue;
import com.caucho.quercus.env.ContinueValue;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;

import edu.iastate.hungnv.empiricalstudy.EmpiricalStudy;

/**
 * Represents a for statement.
 */
public class ForStatement extends Statement {
  protected final Expr _init;
  protected final Expr _test;
  protected final Expr _incr;
  protected final Statement _block;
  protected final String _label;

  public ForStatement(Location location, Expr init, Expr test, Expr incr,
                      Statement block, String label)
  {
    super(location);

    _init = init;
    _test = test;
    _incr = incr;

    _block = block;
    _label = label;
    
    block.setParent(this);
  }

  @Override
  public boolean isLoop()
  {
    return true;
  }

  public Value execute(Env env)
  {
	  // EMPI ADDED BY HUNG
	  EmpiricalStudy.inst.statementExecuted(this, env);
	  // END OF ADDED CODE
	  
    try {
      if (_init != null)
        _init.eval(env);

      while (_test == null || _test.evalBoolean(env)) {
        env.checkTimeout();

        Value value = _block.execute(env);

        if (value == null) {
        }
        else if (value instanceof ContinueValue) {
          ContinueValue conValue = (ContinueValue) value;
          
          int target = conValue.getTarget();
          
          if (target > 1) {
            return new ContinueValue(target - 1);
          }
        }
        else if (value instanceof BreakValue) {
          BreakValue breakValue = (BreakValue) value;
          
          int target = breakValue.getTarget();
          
          if (target > 1)
            return new BreakValue(target - 1);
          else
            break;
        }
        else
          return value;

        if (_incr != null)
          _incr.eval(env);
      }
    }
    catch (RuntimeException t) {
      rethrow(t, RuntimeException.class);
    }

    return null;
  }
}

