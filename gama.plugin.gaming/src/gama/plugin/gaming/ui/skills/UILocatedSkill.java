package gama.plugin.gaming.ui.skills;

import java.util.ArrayList;

import org.locationtech.jts.geom.Envelope;

import gama.annotations.action;
import gama.annotations.arg;
import gama.annotations.doc;
import gama.annotations.example;
import gama.annotations.skill;
import gama.annotations.variable;
import gama.annotations.vars;
import gama.annotations.support.IConcept;
import gama.api.gaml.types.IType;
import gama.api.kernel.agent.IAgent;
import gama.api.kernel.skill.Skill;
import gama.api.runtime.scope.IScope;
import gama.api.types.geometry.GamaPoint;
import gama.api.types.geometry.GamaPointFactory;
import gama.api.ui.IOutput;
import gama.api.utils.geometry.IEnvelope;
import gama.core.outputs.LayeredDisplayOutput;
 
@vars({ @variable(name = IUILocatedSkill.AGENT_LOCATION, type = IType.POINT, doc = @doc("locked location")),
	@variable(name = IUILocatedSkill.AGENT_LOCKED_WIDTH, type = IType.FLOAT, doc = @doc("locked width")),
	@variable(name = IUILocatedSkill.AGENT_LOCKED_HEIGHT, type = IType.FLOAT, doc = @doc("locked height")),
	@variable(name = IUILocatedSkill.AGENT_UI_WIDTH, type = IType.FLOAT, doc = @doc("resized width")),
	@variable(name = IUILocatedSkill.AGENT_UI_HEIGHT, type = IType.FLOAT, doc = @doc("resized height")),
	@variable(name = IUILocatedSkill.AGENT_DISPLAY, type = IType.STRING, doc = @doc("map of location"))})

@skill(name=IUILocatedSkill.SKILL_NAME, concept = { IConcept.GUI, IConcept.COMMUNICATION, IConcept.SKILL })
public class UILocatedSkill extends Skill {
	private ArrayList<IAgent> followedAgent = null;	
	
	@action(name=IUILocatedSkill.UI_AGENT_LOCATION_SET,args={
			@arg(name = IUILocatedSkill.UI_AGENT_LOCATION, type = IType.POINT, optional = false, doc = @doc("location in the display")),
			@arg(name = IUILocatedSkill.UI_NAME, type = IType.STRING, optional = false, doc = @doc("name of the display")),
			@arg(name = IUILocatedSkill.UI_HEIGHT, type = IType.FLOAT, optional = false, doc = @doc("width of the object in %")),
			@arg(name = IUILocatedSkill.UI_WIDTH, type = IType.FLOAT, optional = false, doc = @doc("height of the object in %"))},
			doc = @doc(value = "", returns = "", examples = { @example("")}))
	public void setAgentLocationInUI(IScope scope)
	{
		if(this.followedAgent == null)
			this.initialize(scope);
		IAgent agt = scope.getAgent();
		String outputName = (String) scope.getArg(IUILocatedSkill.UI_NAME, IType.STRING);
		GamaPoint pt = (GamaPoint) scope.getArg(IUILocatedSkill.UI_AGENT_LOCATION, IType.POINT);
		double wd = (Double)scope.getArg(IUILocatedSkill.UI_WIDTH, IType.FLOAT);
		double hg = (Double)scope.getArg(IUILocatedSkill.UI_HEIGHT, IType.FLOAT);
		this.setAgentLocationUI(agt, outputName, pt, wd, hg);
		moveAgentUI(agt);
	}
	
	private void setAgentLocation(IAgent agt, GamaPoint pt)
	{
		agt.setAttribute(IUILocatedSkill.AGENT_LOCATION, pt);
	}
	
	public void setAgentLocationUI(IAgent agt, String outputName, GamaPoint pt, double wd, double hg)
	{
		this.followedAgent.add(agt);
		agt.setAttribute(IUILocatedSkill.AGENT_DISPLAY, outputName);
		setAgentLocation(agt,pt);
		agt.setAttribute(IUILocatedSkill.AGENT_LOCKED_WIDTH, wd);
		agt.setAttribute(IUILocatedSkill.AGENT_LOCKED_HEIGHT, hg);
	}
	
	public void lockAgent(IAgent agt)
	{
		this.followedAgent.add(agt);
	}
	public void unlockAgent(IAgent agt)
	{
		this.followedAgent.remove(agt);
	}
	
	@action(name="refresh_me",args={},
			doc = @doc(value = "", returns = "", examples = { @example("")}))
	public void moveAgentUI(IScope scope)
	{
		IAgent agt = scope.getAgent();
		this.moveAgentUI(agt);
	}
	
	private void moveAgentUI(IAgent agt)
	{
		IScope scope = agt.getScope();
		String outputName = (String) agt.getAttribute(IUILocatedSkill.AGENT_DISPLAY);
		GamaPoint pt = (GamaPoint) agt.getAttribute(IUILocatedSkill.AGENT_LOCATION);
		double ui_width = (double) agt.getAttribute(IUILocatedSkill.AGENT_LOCKED_WIDTH);
		double ui_height = (double) agt.getAttribute(IUILocatedSkill.AGENT_LOCKED_HEIGHT);
		IOutput out = scope.getSimulation().getOutputManager().getOutputWithOriginalName(outputName);
		
		if(!(out instanceof LayeredDisplayOutput))
			return;
		LayeredDisplayOutput output = (LayeredDisplayOutput)out;
		if(output.getSurface() == null)
			return;
		IEnvelope e = output.getSurface().getVisibleRegionForLayer(output.getSurface().getManager().getItems().get(0));
		double xmin=Math.max(0,e.getMinX());
		double ymin=Math.max(0,e.getMinY());
		double xmax = Math.min(e.getMaxX(),output.getSurface().getEnvWidth());
		double ymax=Math.min(e.getMaxY(),output.getSurface().getEnvHeight());
		double xx = xmin + pt.x*(xmax - xmin) ;
		double yy = ymin + pt.y*(ymax - ymin);		
		
		double tui_width = (xmax - xmin) * ui_width;
		double tui_height = (ymax - ymin) * ui_height;
		GamaPoint loc = (GamaPoint) GamaPointFactory.create(xx,yy);
		agt.setAttribute(IUILocatedSkill.AGENT_UI_WIDTH, tui_width);
		agt.setAttribute(IUILocatedSkill.AGENT_UI_HEIGHT, tui_height);
		agt.setLocation(loc);
/*		agt.getScope().execute(scope1 -> { 
			agt.setLocation(loc);
			return null;
		});*/
	}

	@action(name=IUILocatedSkill.UI_AGENT_LOCATION_MOVE,args={
			@arg(name = IUILocatedSkill.UI_AGENT_LOCATION, type = IType.POINT, optional = false, doc = @doc("location in the display"))},
			doc = @doc(value = "", returns = "", examples = { @example("")}))
	public  void moveAgentAt(IScope scope)
	{
		IAgent agt = scope.getAgent();
		GamaPoint pt = (GamaPoint) scope.getArg(IUILocatedSkill.UI_AGENT_LOCATION, IType.POINT);
		this.setAgentLocation(agt, pt);
		moveAgentUI(agt);
	}
	
	public void moveAllAgent()
	{
		for(IAgent a:followedAgent)
		{
			moveAgentUI(a);
		}
	}
	
	private void removeLockedAgent(IScope scope)
	{
		this.followedAgent.clear();
		this.followedAgent=null;
	}
	
	private void removeDeadLockedAgent()
	{
		ArrayList<IAgent> localList = new ArrayList<IAgent>();
		for(IAgent agt:this.followedAgent)
		{
			if(agt.dead())
				localList.add(agt);
		}
		for(IAgent agts:localList)
		{
			this.followedAgent.remove(agts);
		}
	}
	
	protected void initialize(final IScope scope)
	{
		this.followedAgent = new ArrayList<>();
		registerSimulationEvent(scope);
	}
	
	private void registerSimulationEvent(final IScope scope) {
//		scope.getSimulation().getOutputManager().getOutputs().values()
		
		scope.getSimulation().postEndAction(scope1 -> { 
			removeDeadLockedAgent();
			//moveAllAgent();
			return null;
		});

		scope.getSimulation().postDisposeAction(scope1 -> {
			removeLockedAgent(scope1);
			return null;
		});
	}

}
