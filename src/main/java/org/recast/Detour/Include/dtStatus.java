package org.recast.Detour.Include;

/**
 * @author igozha
 * @since 22.09.13 21:56
 */
public class dtStatus
{
	//
	// Copyright (c) 2009-2010 Mikko Mononen memon@inside.org
	//
	// This software is provided 'as-is', without any express or implied
	// warranty.  In no event will the authors be held liable for any damages
	// arising from the use of this software.
	// Permission is granted to anyone to use this software for any purpose,
	// including commercial applications, and to alter it and redistribute it
	// freely, subject to the following restrictions:
	// 1. The origin of this software must not be misrepresented; you must not
	//    claim that you wrote the original software. If you use this software
	//    in a product, an acknowledgment in the product documentation would be
	//    appreciated but is not required.
	// 2. Altered source versions must be plainly marked as such, and must not be
	//    misrepresented as being the original software.
	// 3. This notice may not be removed or altered from any source distribution.
	//

//	#ifndef DETOURSTATUS_H
//	#define DETOURSTATUS_H

	public int dtStatus;

	// High level status.
	public final static int DT_FAILURE = Integer.valueOf("100000000000000000000000000000", 2);//1u << 31;			// Operation failed.
	public final static int DT_SUCCESS = Integer.valueOf("010000000000000000000000000000", 2);//1u << 30;			// Operation succeed.
	public final static int DT_IN_PROGRESS = Integer.valueOf("001000000000000000000000000000", 2);//1u << 29;		// Operation still in progress.

	// Detail information for status.
	public final static int DT_STATUS_DETAIL_MASK = 0x0ffffff;
	public final static int DT_WRONG_MAGIC = 1 << 0;        // Input data is not recognized.
	public final static int DT_WRONG_VERSION = 1 << 1;    // Input data is in wrong version.
	public final static int DT_OUT_OF_MEMORY = 1 << 2;    // Operation ran out of memory.
	public final static int DT_INVALID_PARAM = 1 << 3;    // An input parameter was invalid.
	public final static int DT_BUFFER_TOO_SMALL = 1 << 4;    // Result buffer for the query was too small to store all results.
	public final static int DT_OUT_OF_NODES = 1 << 5;        // Query ran out of nodes during search.
	public final static int DT_PARTIAL_RESULT = 1 << 6;    // Query did not reach the end location, returning best guess.

	public dtStatus(int dtStatus)
	{
		this.dtStatus = dtStatus;
	}

	public dtStatus()
	{
	}

	// Returns true of status is success.
	public static boolean dtStatusSucceed(dtStatus status)
	{
		return (status.dtStatus & DT_SUCCESS) != 0;
	}

	// Returns true of status is failure.
	public static boolean dtStatusFailed(dtStatus status)
	{
		return (status.dtStatus & DT_FAILURE) != 0;
	}

	// Returns true of status is in progress.
	public static boolean dtStatusInProgress(dtStatus status)
	{
		return (status.dtStatus & DT_IN_PROGRESS) != 0;
	}

	// Returns true if specific detail is set.
	public static boolean dtStatusDetail(dtStatus status, int detail)
	{
		return (status.dtStatus & detail) != 0;
	}

//	#endif // DETOURSTATUS_H

}
