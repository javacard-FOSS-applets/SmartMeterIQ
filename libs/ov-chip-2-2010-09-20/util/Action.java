// 
// OV-chip 2.0 project
// 
// Digital Security (DS) group at Radboud Universiteit Nijmegen
// 
// Copyright (C) 2008, 2009
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of
// the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License in file COPYING in this or one of the
// parent directories for more details.
// 
// Created 6.1.09 by Hendrik
// 
// a class implementing Runnable
// 
// $Id: Action.java,v 1.6 2009-03-26 15:51:31 tews Exp $

package ds.ov2.util;


/** 
 * An implementation of {@link Runnable} to be able to create 
 * anonymous inner runnables that implement {@link Runnable}.
 *
 * @author Hendrik Tews
 * @version $Revision: 1.6 $
 * @commitdate $Date: 2009-03-26 15:51:31 $ by $Author: tews $
 * @environment host
 * @CPP no cpp preprocessing needed
 */
public class Action implements Runnable {
    /**
     * 
     * Empty constructor.
     */
    public Action() {}
    

    /**
     * 
     * The run method. Classes extending this class should override
     * this method because it asserts false.
     */
    public void run() {
        assert false;
    }
}

