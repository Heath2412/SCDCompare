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
import com.caucho.quercus.expr.AbstractVarExpr;
import com.caucho.quercus.expr.Expr;

import edu.iastate.hungnv.empiricalstudy.EmpiricalStudy;
import edu.iastate.hungnv.shadow.Env_;
import edu.iastate.hungnv.shadow.ForeachStatement_;
import edu.iastate.hungnv.shadow.ShadowInterpreter;
import edu.iastate.hungnv.value.MultiValue;

import java.util.Iterator;
import java.util.Map;

/**
 * Represents a foreach statement.
 */
public class ForeachStatement
  extends Statement
{
  protected final Expr _objExpr;

  protected final AbstractVarExpr _key;

  protected final AbstractVarExpr _value;
  protected final boolean _isRef;

  protected final Statement _block;

  protected final String _label;

  public ForeachStatement(Location location,
                          Expr objExpr,
                          AbstractVarExpr key,
                          AbstractVarExpr value,
                          boolean isRef,
                          Statement block,
                          String label)
  {
    super(location);

    _objExpr = objExpr;

    _key = key;
    _value = value;
    _isRef = isRef;

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
	  
    Value origObj = _objExpr.eval(env);
    Value obj = origObj.copy(); // php/0669
    
    // INST ADDED BY HUNG
    if (Env_.INSTRUMENT) {
	    if (obj instanceof MultiValue) {
	    	final ForeachStatement this_ = this;
			
	    	Value retValue = ShadowInterpreter.eval(obj, new ShadowInterpreter.IBasicCaseHandler() {
				@Override
				public Value evalBasicCase(Value flattenedObj, Env env) {
					return this_.execute_basic(env, flattenedObj, flattenedObj);
				}
			}, env);
			
        	if (retValue instanceof MultiValue) {
            	// TODO Handle returned MultiValue here
        		return null;
	        }
        	else
        		return retValue;
	    }
	    else
	    	return execute_basic(env, origObj, obj);
    }
    // END OF ADDED CODE

    if (_key == null && ! _isRef) {
      Iterator<Value> iter = obj.getValueIterator(env);

      while (iter.hasNext()) {
        Value value = iter.next();

        value = value.copy(); // php/0662

        _value.evalAssignValue(env, value);

        Value result = _block.execute(env);

        if (result == null) {
        }
        else if (result instanceof ContinueValue) {
          ContinueValue conValue = (ContinueValue) result;

          int target = conValue.getTarget();

          if (target > 1) {
            return new ContinueValue(target - 1);
          }
        }
        else if (result instanceof BreakValue) {
          BreakValue breakValue = (BreakValue) result;

          int target = breakValue.getTarget();

          if (target > 1)
            return new BreakValue(target - 1);
          else
            break;
        }
        else
          return result;
      }

      return null;
    } else if (_isRef) {
      Iterator<Value> iter = obj.getKeyIterator(env);

      while (iter.hasNext()) {
        Value key = iter.next();

        if (_key != null)
          _key.evalAssignValue(env, key);

        Value value = origObj.getVar(key);

        // php/0667
        _value.evalAssignRef(env, value);

        Value result = _block.execute(env);

        if (result == null) {
        }
        else if (result instanceof ContinueValue) {
          ContinueValue conValue = (ContinueValue) result;

          int target = conValue.getTarget();

          if (target > 1) {
            return new ContinueValue(target - 1);
          }
        }
        else if (result instanceof BreakValue) {
          BreakValue breakValue = (BreakValue) result;

          int target = breakValue.getTarget();

          if (target > 1)
            return new BreakValue(target - 1);
          else
            break;
        }
        else
          return result;
      }
    }
    else {
      Iterator<Map.Entry<Value,Value>> iter = obj.getIterator(env);

      while (iter.hasNext()) {
        Map.Entry<Value, Value> entry = iter.next();
        Value key = entry.getKey();
        Value value = entry.getValue();

        value = value.copy(); // php/066w

        _key.evalAssignValue(env, key);

        _value.evalAssignValue(env, value);

        Value result = _block.execute(env);

        if (result == null) {
        }
        else if (result instanceof ContinueValue) {
          ContinueValue conValue = (ContinueValue) result;

          int target = conValue.getTarget();

          if (target > 1) {
            return new ContinueValue(target - 1);
          }
        }
        else if (result instanceof BreakValue) {
          BreakValue breakValue = (BreakValue) result;

          int target = breakValue.getTarget();

          if (target > 1)
            return new BreakValue(target - 1);
          else
            break;
        }
        else
          return result;
      }
    }

    return null;
  }
  
// INST ADDED BY HUNG
  private Value execute_basic(Env env, Value origObj, Value obj) {
	    if (_key == null && ! _isRef) {
	      Iterator<Value> iter = obj.getValueIterator(env);

	      while (iter.hasNext()) {
	        Value value = iter.next();

	        value = value.copy(); // php/0662
	        
	        // INST MODIFIED BY HUNG
	        
	        Value result = ForeachStatement_.execute(env, value, _value, _block);
	        	
        	if (result instanceof MultiValue) {
            	// TODO Handle returned MultiValue here
        		continue;
	        }

//			Original code:
//	      
//	        _value.evalAssignValue(env, value);
//
//	        Value result = _block.execute(env);
	        
	        // END OF MODIFIED CODE

	        if (result == null) {
	        }
	        else if (result instanceof ContinueValue) {
	          ContinueValue conValue = (ContinueValue) result;

	          int target = conValue.getTarget();

	          if (target > 1) {
	            return new ContinueValue(target - 1);
	          }
	        }
	        else if (result instanceof BreakValue) {
	          BreakValue breakValue = (BreakValue) result;

	          int target = breakValue.getTarget();

	          if (target > 1)
	            return new BreakValue(target - 1);
	          else
	            break;
	        }
	        else
	          return result;
	      }

	      return null;
	    } else if (_isRef) {
	      Iterator<Value> iter = obj.getKeyIterator(env);

	      while (iter.hasNext()) {
	        Value key = iter.next();

	        if (_key != null)
	          _key.evalAssignValue(env, key);

	        Value value = origObj.getVar(key);

	        // php/0667
	        _value.evalAssignRef(env, value);

	        Value result = _block.execute(env);

	        if (result == null) {
	        }
	        else if (result instanceof ContinueValue) {
	          ContinueValue conValue = (ContinueValue) result;

	          int target = conValue.getTarget();

	          if (target > 1) {
	            return new ContinueValue(target - 1);
	          }
	        }
	        else if (result instanceof BreakValue) {
	          BreakValue breakValue = (BreakValue) result;

	          int target = breakValue.getTarget();

	          if (target > 1)
	            return new BreakValue(target - 1);
	          else
	            break;
	        }
	        else
	          return result;
	      }
	    }
	    else {
	      Iterator<Map.Entry<Value,Value>> iter = obj.getIterator(env);

	      while (iter.hasNext()) {
	        Map.Entry<Value, Value> entry = iter.next();
	        Value key = entry.getKey();
	        Value value = entry.getValue();

	        value = value.copy(); // php/066w

	        _key.evalAssignValue(env, key);

	        _value.evalAssignValue(env, value);

	        Value result = _block.execute(env);

	        if (result == null) {
	        }
	        else if (result instanceof ContinueValue) {
	          ContinueValue conValue = (ContinueValue) result;

	          int target = conValue.getTarget();

	          if (target > 1) {
	            return new ContinueValue(target - 1);
	          }
	        }
	        else if (result instanceof BreakValue) {
	          BreakValue breakValue = (BreakValue) result;

	          int target = breakValue.getTarget();

	          if (target > 1)
	            return new BreakValue(target - 1);
	          else
	            break;
	        }
	        else
	          return result;
	      }
	    }

	    return null;
	  }
// END OF ADDED CODE
  
}

