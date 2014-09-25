package br.ufmg.dcc.labsoft.jextract.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;

import br.ufmg.dcc.labsoft.jextract.codeanalysis.Utils;
import br.ufmg.dcc.labsoft.jextract.generation.EmrGenerator;
import br.ufmg.dcc.labsoft.jextract.generation.Settings;
import br.ufmg.dcc.labsoft.jextract.ranking.ExtractMethodRecomendation;

public class ProjectMenuAction extends ObjectMenuAction<IProject> {

	public ProjectMenuAction() {
		super(IProject.class);
	}

	@Override
	public void handleAction(IAction action, List<IProject> projects) throws Exception {
		String actionId = action.getId();
		final IProject project = projects.get(0);
		if (actionId.equals("br.ufmg.dcc.labsoft.jextract.findEmr")) {
			findEmr(project);
		}
	}

	private void findEmr(final IProject project) throws Exception {
		EmrSettingsDialog dialog = new EmrSettingsDialog(this.getShell());
		if (dialog.open() == Window.OK) {
			final Settings settings = dialog.getSettings();
			final List<ExtractMethodRecomendation> recomendations = new ArrayList<ExtractMethodRecomendation>();
			final EmrGenerator generator = new EmrGenerator(recomendations, settings);
			
			JobRunner.run("Finding Recommendations", new ItemProcessingJob<ICompilationUnit>(){
				public List<ICompilationUnit> getItems(){
					return Utils.findJavaResources(project);
				}
				public void processItem(ICompilationUnit icu) throws Exception {
					generator.generateRecomendations(icu);
				}
				public void updateUI() throws Exception {
					showResultView(recomendations, project, settings);
				}
			});
		}
	}

}
