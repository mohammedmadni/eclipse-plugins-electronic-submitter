/*
 *	This file is part of Web-CAT Eclipse Plugins.
 *
 *	Web-CAT is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation; either version 2 of the License, or
 *	(at your option) any later version.
 *
 *	Web-CAT is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with Web-CAT; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.webcat.eclipse.submitter.ui.wizards;

import java.util.ArrayList;

import net.sf.webcat.eclipse.submitter.ui.dialogs.SubmissionParserErrorDialog;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.webcat.submitter.targets.ImportGroupTarget;
import org.webcat.submitter.targets.SubmissionTarget;

/**
 * The content provider for the tree that displays the submission targets in the
 * wizard.
 * 
 * @author Tony Allevato
 */
public class SubmissionTargetsContentProvider implements ITreeContentProvider
{
	// === Methods ============================================================

	// ------------------------------------------------------------------------
	/**
	 * Creates a new instance of the content provider.
	 * 
	 * @param context
	 *            The context on which to execute operations.
	 */
	public SubmissionTargetsContentProvider(IRunnableContext context)
	{
		this.context = context;
	}


	// ------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		SubmissionTarget obj = (SubmissionTarget)parentElement;

		ArrayList<SubmissionTarget> children =
			new ArrayList<SubmissionTarget>();
		computeChildren(obj, children);
		return children.toArray();
	}


	// ------------------------------------------------------------------------
	/**
	 * Computes the visible children of the specified node, displaying a message
	 * to the user if any errors occur.
	 */
	private void computeChildren(SubmissionTarget obj,
			                     ArrayList<SubmissionTarget> list)
	{
		try
		{
			SubmissionTarget[] children = obj.getLogicalChildren();
			for(int i = 0; i < children.length; i++)
			{
				SubmissionTarget child = children[i];

				if(!child.isHidden())
				{
					if(child.isContainer() && !child.isNested())
						computeChildren(child, list);
					else
						list.add(child);
				}
			}
		}
		catch(Throwable e)
		{
			SubmissionParserErrorDialog dlg = new SubmissionParserErrorDialog(
			        null, e);
			dlg.open();

			list.clear();
		}
	}


	// ------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		return ((SubmissionTarget)element).parent();
	}


	// ------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		if(element instanceof ImportGroupTarget)
		{
			// If it's an imported group, it might have children.
			// Chances are it does. We want expand logic here.
			return true;
		}
		else
		{
			ArrayList<SubmissionTarget> children =
				new ArrayList<SubmissionTarget>();
			computeChildren((SubmissionTarget)element, children);
			return children.size() > 0;
		}
	}


	// ------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		return getChildren(root);
	}


	// ------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}


	// ------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		root = (SubmissionTarget) newInput;
	}

	
	// === Instance Variables =================================================

	/**
	 * The root of the submission target tree.
	 */
	private SubmissionTarget root;

	/**
	 * The context on which to execute submission target tree operations.
	 */
	private IRunnableContext context;
}
