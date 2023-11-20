package LNZModule;

import Misc.Misc;

class StatusEffect {
  private LNZ p;

  protected boolean permanent = false;
  protected double timer_gone_start = 0;
  protected double timer_gone = 0;
  protected double number = 0; // usually a timer for DoTs
  protected DamageSource damage_source;

  StatusEffect(LNZ sketch) {
    this.p = sketch;
    this.damage_source = new DamageSource(4);
  }
  StatusEffect(LNZ sketch, StatusEffectCode code, double timer) {
    this(sketch, code, timer, false);
  }
  StatusEffect(LNZ sketch, StatusEffectCode code, boolean permanent) {
    this(sketch, code, 0, permanent);
  }
  StatusEffect(LNZ sketch, StatusEffectCode code, double timer, boolean permanent) {
    this.p = sketch;
    this.damage_source = new DamageSource(4, code);
    this.timer_gone_start = timer;
    this.timer_gone = timer;
    this.permanent = permanent;
    switch(code) {
      case HUNGRY:
        this.number = Misc.randomDouble(LNZ.status_hunger_tickTimer);
        break;
      case THIRSTY:
        this.number = Misc.randomDouble(LNZ.status_thirst_tickTimer);
        break;
      case WOOZY:
        this.number = Misc.randomDouble(LNZ.status_woozy_tickMaxTimer);
        break;
      case CONFUSED:
        this.number = Misc.randomDouble(LNZ.status_confused_tickMaxTimer);
        break;
      case BLEEDING:
        this.number = Misc.randomDouble(LNZ.status_bleed_tickTimer);
        break;
      case HEMORRHAGING:
        this.number = Misc.randomDouble(LNZ.status_hemorrhage_tickTimer);
        break;
      case UNKILLABLE:
        this.number = 1;
        break;
      default:
        break;
    }
  }

  void update(int millis) {
    if (!this.permanent) {
      this.timer_gone -= millis;
    }
  }

  void startTimer(double timer) {
    this.permanent = false;
    this.timer_gone_start = timer;
    this.timer_gone = timer;
  }

  void addTime(double extra_time) {
    this.timer_gone_start += extra_time;
    this.timer_gone += extra_time;
  }

  void refreshTime(double time) {
    this.timer_gone = Math.max(this.timer_gone, time);
    this.timer_gone_start = this.timer_gone;
  }

  String fileString() {
    String fileString = "\nnew: StatusEffect";
    fileString += "\npermanent: " + this.permanent;
    fileString += "\ntimer_gone_start: " + this.timer_gone_start;
    fileString += "\ntimer_gone: " + this.timer_gone;
    fileString += "\nnumber: " + this.number;
    fileString += "\nend: StatusEffect";
    return fileString;
  }

  void addData(String datakey, String data) {
    switch(datakey) {
      case "permanent":
        this.permanent = Misc.toBoolean(data);
        break;
      case "timer_gone_start":
        this.timer_gone_start = Misc.toDouble(data);
        break;
      case "timer_gone":
        this.timer_gone = Misc.toDouble(data);
        break;
      case "number":
        this.number = Misc.toDouble(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not found for StatusEffect data.");
        break;
    }
  }
}