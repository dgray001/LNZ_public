package Button;

import processing.core.*;
import LNZApplet.LNZApplet;

// shaped like the 'Find Match' button in League
public abstract class LeagueButton extends ArcButton {
  protected double trapezoid_height;
  protected double trapezoid_shift;
  protected double trapezoid_xi;
  protected double trapezoid_xf;
  protected double trapezoid_bottom;
  protected PVector[] vertices = new PVector[4]; // trapezoid vertices

  public LeagueButton(LNZApplet sketch, double xBottom, double yBottom, double xRadius,
    double yRadius, double radians, double trapezoid_height, double trapezoid_shift) {
    super(sketch, xBottom, yBottom - yRadius, xRadius, yRadius, PApplet.HALF_PI - 0.5 * radians, PApplet.HALF_PI + 0.5 * radians);
    this.pie = false;
    this.trapezoid_height = trapezoid_height;
    this.trapezoid_shift = trapezoid_shift;
    this.trapezoid_xi = xStop * this.xr + this.xc;
    this.trapezoid_xf = xStart * this.xr + this.xc;
    this.trapezoid_bottom = yStop * this.yr + this.yc;
    this.vertices[0] = new PVector((float)this.trapezoid_xi, (float)this.trapezoid_bottom);
    this.vertices[1] = new PVector((float)(this.trapezoid_xi + this.trapezoid_shift),
      (float)(this.trapezoid_bottom - this.trapezoid_height));
    this.vertices[2] = new PVector((float)(this.trapezoid_xf - this.trapezoid_shift),
      (float)(this.trapezoid_bottom - this.trapezoid_height));
    this.vertices[3] = new PVector((float)this.trapezoid_xf, (float)this.trapezoid_bottom);
  }

  @Override
  public void drawButton() {
    this.setFill();
    p.ellipseMode(PConstants.RADIUS);
    p.arc(this.xc, this.yc, this.xr, this.yr, this.start, this.stop, PConstants.OPEN);
    p.beginShape();
    for (PVector vec : this.vertices) {
      p.vertex(vec.x, vec.y);
    }
    p.endShape();
    p.stroke(this.fillColor());
    p.strokeWeight(1);
    p.line(this.trapezoid_xi+2, this.trapezoid_bottom-1, this.trapezoid_xf-2, this.trapezoid_bottom-1);
    if (this.show_message) {
      p.fill(this.color_text);
      p.textSize(this.text_size);
      p.textAlign(PConstants.CENTER, PConstants.BOTTOM);
      p.text(this.message, this.xc, this.trapezoid_bottom);
    }
  }

  public boolean mouseOn(float mX, float mY) {
    boolean collision = false;
    for (int i = 0; i < this.vertices.length; i++) {
      PVector v1 = this.vertices[i];
      PVector v2;
      if (i + 1 == this.vertices.length) {
        v2 = this.vertices[0];
      }
      else {
        v2 = this.vertices[i + 1];
      }
      if ( ((v1.y > mY) != (v2.y > mY)) && (mX < (v2.x - v1.x) * (mY - v1.y) / (v2.y - v1.y) + v1.x) ) {
        collision = !collision;
      }
    }
    if (collision) {
      return true;
    }
    if (super.mouseOn(mX, mY)) {
      return true;
    }
    return false;
  }
}