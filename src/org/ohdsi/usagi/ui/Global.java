/*******************************************************************************
 * Copyright 2016 Observational Health Data Sciences and Informatics
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ohdsi.usagi.ui;

import javax.swing.JFrame;

import org.ohdsi.usagi.UsagiSearchEngine;
import org.ohdsi.usagi.ui.actions.AboutAction;
import org.ohdsi.usagi.ui.actions.ApplyPreviousMappingAction;
import org.ohdsi.usagi.ui.actions.ApproveAction;
import org.ohdsi.usagi.ui.actions.ApproveAllAction;
import org.ohdsi.usagi.ui.actions.ClearAllAction;
import org.ohdsi.usagi.ui.actions.ConceptInformationAction;
import org.ohdsi.usagi.ui.actions.ExitAction;
import org.ohdsi.usagi.ui.actions.ExportSourceToConceptMapAction;
import org.ohdsi.usagi.ui.actions.ImportAction;
import org.ohdsi.usagi.ui.actions.OpenAction;
import org.ohdsi.usagi.ui.actions.RebuildIndexAction;
import org.ohdsi.usagi.ui.actions.SaveAction;
import org.ohdsi.usagi.ui.actions.SaveAsAction;

public class Global {
	public static JFrame							frame;
	public static Mapping							mapping;
	public static UsagiSearchEngine					usagiSearchEngine;
	public static String							folder;
	public static String							filename;

	public static MappingDetailPanel				mappingDetailPanel;
	public static MappingTablePanel					mappingTablePanel;
	public static ConceptInformationDialog			conceptInformationDialog;
	public static UsagiStatusBar					statusBar;

	public static OpenAction						openAction;
	public static ApplyPreviousMappingAction		applyPreviousMappingAction;
	public static ImportAction						importAction;
	public static SaveAction						saveAction;
	public static SaveAsAction						saveAsAction;
	public static ApproveAction						approveAction;
	public static ApproveAllAction					approveAllAction;
	public static ClearAllAction					clearAllAction;
	public static ConceptInformationAction			conceptInfoAction;
	public static AboutAction						aboutAction;
	public static ExportSourceToConceptMapAction	exportAction;
	public static RebuildIndexAction				rebuildIndexAction;
	public static ExitAction						exitAction;
}
