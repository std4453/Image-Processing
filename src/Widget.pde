import java.awt.*;

public static interface Widget {
  public void draw();
  public Dimension getSize();
  public void setLocation(Point position);
  //public void mouseEnter(Point relative);
  //public void mouseLeave(Point relative);
  //public void mouseDown(Point relative, int button);
  //public void mouseUp(Point relative, int button);
  public void setEnabled(boolean enabled, Object... params);
}


// --------------------------------------------------------------------------------------
//                                       CHECK WIDGET 
// --------------------------------------------------------------------------------------


public static class CheckWidget extends MouseAdapter implements Widget {
  // View-related fields
  protected Point position;
  protected boolean mouseOver, mouseDown;
  protected boolean enabled = true;
  
  // Model-related fields
  protected boolean checked;
  protected String label;
  protected CheckListener listener;
  
  protected UIManager ui;
  
  public CheckWidget(UIManager ui, String label, CheckListener listener, boolean initial) {
    this.ui = ui;
    
    this.label = label;
    this.listener = listener;
    this.checked = initial;
  }
  
  public void draw() {
    PApplet applet = this.ui.applet;
    
    int x = this.position.x, y = this.position.y;
    applet.noFill();
    applet.stroke(this.enabled ? 0 : 100);
    applet.rect(x, y, 14, 14);
    
    if (this.checked) {
      applet.line(x + 3, y + 7, x + 6, y + 10);
      applet.line(x + 6, y + 10, x + 11, y + 4);
    }
    
    if (!this.enabled) {
      applet.noStroke();
      applet.fill(0, 30);
      applet.rect(x, y, 14, 14);
    } else if (this.mouseOver || this.mouseDown) {
      applet.noStroke();
      applet.fill(0, this.mouseDown ? 100 : 50);
      applet.rect(x, y, 14, 14);
    }
    
    applet.noStroke();
    applet.fill(this.enabled ? 0 : 100);
    applet.textSize(12);
    applet.textAlign(LEFT, BOTTOM);
    applet.text(this.label, x + 18, y + 13);
  }
  
  private void onClick() {
    if (!this.enabled)
      return;
    
    this.checked = !this.checked;
    if (this.listener != null)
      this.listener.onChange(this.checked);
    this.ui.requestRedraw();
  }
  
  public void setEnabled(boolean enabled, Object... params) {
    this.enabled = enabled;
  }
  
  public Dimension getSize() {
    PApplet applet = this.ui.applet;
    applet.textSize(12);
    float textWidth = applet.textWidth(this.label);
    return new Dimension(14 + 4 + (int)textWidth, 14);
  }
  
  public void setLocation(Point position) {
    this.position = new Point(position);
    
    this.ui.listen(new Rectangle(position, this.getSize()), this);
  }
  
  @Override
  public void mouseEntered(java.awt.event.MouseEvent unused) {
    this.mouseOver = true;
    this.ui.requestRedrawWidgets();
  }
  @Override
  public void mouseExited(java.awt.event.MouseEvent unused) {
    this.mouseOver = false;
    this.mouseDown = false;
    this.ui.requestRedrawWidgets();
  }
  @Override
  public void mousePressed(java.awt.event.MouseEvent unused) {
    this.mouseDown = true;
    this.ui.requestRedrawWidgets();
  }
  @Override
  public void mouseReleased(java.awt.event.MouseEvent event) {
    if (this.mouseDown && event.getButton() == Mouse.LEFT_BUTTON)
      this.onClick();
    this.mouseDown = false;
    this.ui.requestRedrawWidgets();
  }
}
public static interface CheckListener {
  public void onChange(boolean value);
}


// --------------------------------------------------------------------------------------
//                                       RADIO WIDGET 
// --------------------------------------------------------------------------------------


public static class RadioWidget implements Widget {
  // View-related fields
  protected Point position;
  protected int mouseOver = -1, mouseDown = -1;
  protected boolean[] enabled;
  
  // Model-related fields
  protected int choice;
  protected String[] labels;
  protected RadioListener listener;
  protected int length;
  
  protected UIManager ui;
  
  public RadioWidget(UIManager ui, String[] labels, RadioListener listener, int initial) {
    this.ui = ui;
    
    this.labels = labels;
    this.listener = listener;
    if (initial < 0 || initial >= labels.length)
      initial = 0;
    this.choice = initial;
    this.length = this.labels.length;
    this.enabled = new boolean[this.length];
    Arrays.fill(this.enabled, true);
  }
  
  public void draw() {
    PApplet applet = this.ui.applet;
    
    int x = this.position.x, y = this.position.y;
    
    for (int i = 0; i < this.length; ++i) {
      applet.noFill();
      applet.stroke(this.enabled[i] ? 0 : 100);
      applet.ellipse(x + 7, y + 7, 14, 14);
      
      if (this.choice == i) {
        applet.noStroke();
        applet.fill(this.enabled[i] ? 0 : 100);
        applet.ellipse(x + 7.5f, y + 7.5f, 6, 6);
      }
      
      if (!this.enabled[i]) {
        applet.noStroke();
        applet.fill(0, 30);
        applet.ellipse(x + 7, y + 7, 14, 14);
      } else if (this.mouseOver == i || this.mouseDown == i) {
        applet.noStroke();
        applet.fill(0, this.mouseDown == i ? 100 : 50);
        applet.ellipse(x + 7, y + 7, 14, 14);
      }
      
      applet.noStroke();
      applet.fill(this.enabled[i] ? 0 : 100);
      applet.textAlign(LEFT, BOTTOM);
      applet.textSize(12);
      applet.text(this.labels[i], x + 18, y + 13);
      
      x += 14 + 4 + applet.textWidth(this.labels[i]) + 10;
    }
  }
  
  private void onClick(int i) {
    if (!this.enabled[i])
      return;
    if (this.choice == i)
      return;
    
    this.choice = i;
    if (this.listener != null)
      this.listener.onChange(this.choice);
    this.ui.requestRedraw();
  }
  
  public void setEnabled(boolean enabled, Object... params) {
    if (params.length == 0 || !(params[0] instanceof Integer))
      return;
    int index = (Integer)params[0];
    if (index >=0 && index < this.length)
      this.enabled[index] = enabled;
  }
  
  public Dimension getSize() {
    PApplet applet = this.ui.applet;
    applet.textSize(12);
    int textWidth = 18 * this.length + 10 * (this.length - 1);
    for (int i = 0; i < this.length; ++i) textWidth += applet.textWidth(this.labels[i]);
    return new Dimension(textWidth, 14);
  }
  
  public void setLocation(Point position) {
    this.position = new Point(position);
    PApplet applet = this.ui.applet;
    applet.textSize(12);
    int x = position.x;
    for (int i = 0; i < this.length; ++i) {
      int width = 18 + (int)applet.textWidth(this.labels[i]);
      final int ii = i;
      this.ui.listen(new Rectangle(x, position.y, width, 14), new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent unused) {
              RadioWidget.this.mouseOver = ii;
              RadioWidget.this.ui.requestRedrawWidgets();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent unused) {
              if (RadioWidget.this.mouseOver == ii)
                RadioWidget.this.mouseOver = -1;
              if (RadioWidget.this.mouseDown == ii)
                RadioWidget.this.mouseDown = -1;
              RadioWidget.this.ui.requestRedrawWidgets();
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent unused) {
              RadioWidget.this.mouseDown = ii;
              RadioWidget.this.ui.requestRedrawWidgets();
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent event) {
              if (RadioWidget.this.mouseDown == ii && event.getButton() == Mouse.LEFT_BUTTON) {
                RadioWidget.this.onClick(ii);
              }
              if (RadioWidget.this.mouseDown == ii)
                RadioWidget.this.mouseDown = -1;
              RadioWidget.this.ui.requestRedrawWidgets();
            }
        });
      x += width + 10;
    }
  }
}
public static interface RadioListener {
  public void onChange(int value);
}