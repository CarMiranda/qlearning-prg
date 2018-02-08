package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.agents.LearningAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.LearningTask;
import ch.idsia.tools.EvaluationInfo;

public class ForwardJumpingLearningAgent implements LearningAgent{

  boolean[] action;
  
  public boolean[] getAction() {
    // TODO Auto-generated method stub
    return action;
  }

  public void integrateObservation(Environment environment) {
    // TODO Auto-generated method stub
    action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] = 
          environment.isMarioAbleToJump() || !environment.isMarioOnGround();
    
  }

  public void giveIntermediateReward(float intermediateReward) {
    // TODO Auto-generated method stub
    
  }

  public void reset() {
    // TODO Auto-generated method stub
    
  }

  public void setObservationDetails(int rfWidth, int rfHeight, int egoRow,
      int egoCol) {
    // TODO Auto-generated method stub
    
  }

  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  public void setName(String name) {
    // TODO Auto-generated method stub
    
  }

  public void learn() {
    // TODO Auto-generated method stub
    
  }

  public void giveReward(float reward) {
    // TODO Auto-generated method stub
    
  }

  public void newEpisode() {
    // TODO Auto-generated method stub
    
  }

  public void setLearningTask(LearningTask learningTask) {
    // TODO Auto-generated method stub
    
  }

  public void setEvaluationQuota(long num) {
    // TODO Auto-generated method stub
    
  }

  public Agent getBestAgent() {
    // TODO Auto-generated method stub
    return this;
  }

  public void init() {
    // TODO Auto-generated method stub
    action = new boolean[6];
    action[Mario.KEY_RIGHT] = true;
  }

}
