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
import com.caucho.quercus.QuercusException;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;

import edu.iastate.hungnv.debug.OutputViewer;
import edu.iastate.hungnv.empiricalstudy.EmpiricalStudy;
import edu.iastate.hungnv.shadow.Env_;
import edu.iastate.hungnv.util.Logging;

import java.io.IOException;

/**
 * Represents static text in a PHP program.
 */
public class TextStatement extends Statement {
  private String _value;
  
  /**
   * Creates the text statement with its string.
   */
  public TextStatement(Location location, String value)
  {
    super(location);

    _value = value;
  }

  protected String getValue()
  {
    return _value;
  }
  
  public Value execute(Env env)
  {
	  // EMPI ADDED BY HUNG
	  EmpiricalStudy.inst.statementExecuted(this, env);
	  // END OF ADDED CODE
	  
    try {
    	// INST ADDED BY HUNG
    	
    	if (Env_.INSTRUMENT)
    		Logging.LOGGER.info("Printing: " + _value.toString());
    	
    	// END OF ADDED CODE
    	
        // INST MODIFIED BY HUNG
        
        // Original code:
        //env.getOut().print(_value);
        
        // New code:
    	
    	// NOTE: This code is not guarded by if (Env_.INSTRUMENT)
        
    	boolean curState = OutputViewer.inst.getEnabled();
    	OutputViewer.inst.setEnabled(false);
    	
        env.getOut().print(_value);
    	
        OutputViewer.inst.setEnabled(curState);
        
        OutputViewer.inst.print(_value);
      
      	// END OF MODIFIED CODE
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (IOException e) {
      throw new QuercusException(e);
    }

    return null;
  }
}

