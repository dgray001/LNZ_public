package Element;

import LNZApplet.LNZApplet;

public abstract class MaxListTextBox extends ListTextBox {
  protected double y_curr = 0;

  public MaxListTextBox(LNZApplet sketch) {
    this(sketch, 0, 0, 0, 0);
  }
  public MaxListTextBox(LNZApplet sketch, double xi, double yi, double xf, double yf) {
    super(sketch, xi, yi, xf, yf);
  }

  @Override
  public void setText(String text) {
    super.setText(text);
    double currY = this.yi + 3;
    if (this.text_title_ref != null) {
      p.textSize(this.title_size);
      currY += p.textAscent() + p.textDescent() + 2;
    }
    p.textSize(this.text_size);
    double text_height = p.textAscent() + p.textDescent();
    this.y_curr = Math.min(this.yf, currY + this.text_lines_ref.size() * (text_height + this.text_leading));
  }

  @Override
  public void update(int millis) {
    double y_max = this.yf;
    this.yf = y_curr;
    super.update(millis);
    this.yf = y_max;
  }
}