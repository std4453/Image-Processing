public static class EntryMosaic implements Entry {
  private class Context {
    public boolean resize = true;
    public boolean fade = false;
    public boolean circle = true;
    public boolean fill = true;
    public boolean posterize = true;
    public boolean posterized = false;
    public int diamt = 8, spacing = 8;
    
    public String fileName = "";
    public PImage image = null;
   
    public UIManager ui;
    
    public Context(String fileName, UIManager ui) {
      this.fileName = fileName;
      this.ui = ui;
    }
    
    public void setImage(PImage image) {
      this.image = image;
    }
    public void setResize(boolean resize) {
      this.resize = resize;
    }
    public void setFade(boolean fade) {
      this.fade = fade;
    }
    public void setCircle(boolean circle) {
      this.circle = circle;
    }
    public void setPosterize(boolean posterize) {
      this.posterize = posterize;
    }
    public void setPosterized(boolean posterized) {
      this.posterized = posterized;
    }
  }
  
  protected Context context;
  
  public EntryMosaic(String fileName, UIManager applet) {
    this.context = new Context(fileName, applet);
  }
  
  @Override
  public void setup() {
    this.addControls();
    
    this.context.setImage(this.context.ui.applet.loadImage(this.context.fileName));
    
    boolean fill = this.context.fill;
    int spacing = this.context.spacing;
    this.context.ui.setCanvasSize(this.context.image.width - (fill ? spacing : 0), this.context.image.height - (fill ? spacing: 0));
  }
  
  private void addControls() {
    this.context.ui.addCheck("resize", new CheckListener() {
        @Override
        public void onChange(boolean value) {
          EntryMosaic.this.context.setResize(value);
        }
    }, this.context.resize);
    this.context.ui.addCheck("fade", new CheckListener() {
        @Override
        public void onChange(boolean value) {
          EntryMosaic.this.context.setFade(value);
        }
    }, this.context.fade);
    this.context.ui.addCheck("circle", new CheckListener() {
        @Override
        public void onChange(boolean value) {
          EntryMosaic.this.context.setCircle(value);
        }
    }, this.context.circle);
    this.context.ui.addCheck("posterize", new CheckListener() {
        @Override
        public void onChange(boolean value) {
          EntryMosaic.this.context.setPosterize(value);
        }
    }, this.context.posterize);
  }
  
  @Override
  public void draw() {
    PApplet applet = this.context.ui.applet;
    applet.background(255);
    
    if (this.context.posterize ^ this.context.posterized) {
      PImage image = this.context.ui.applet.loadImage(this.context.fileName);
      if (this.context.posterize)
        image.filter(POSTERIZE, 8);
      this.context.setPosterized(this.context.posterize);
      this.context.setImage(image);
    }
    
    boolean fill = this.context.fill, resize = this.context.resize, fade = this.context.fade, circle = this.context.circle;
    int spacing = this.context.spacing, diamt = this.context.diamt;
    int width = this.context.image.width, height = this.context.image.height;
    
    int dx = fill ? - spacing / 2 : 0, dy = fill ? - spacing / 2 : 0;
    
    // cover all the surface
    int i0 = width % spacing / 2;
    int j0 = height % spacing / 2;
    for (int i = i0; i < width; i += spacing) {
      for (int j = j0; j < height; j += spacing) {
        int c = this.context.image.get(i, j);
        
        // set color
        applet.fill(c);
        applet.noStroke();
        
        // effects
        float diamt2 = diamt;
        int r = (c & 0xFF0000) >> 16, g = (c & 0xFF00) >> 8, b = c & 0xFF;      
        float avg = (r + g + b) / 3.0f;
        
        if (resize) {
          // calc lumination
          float lum = 1 - avg / 255.0f * 0.5;
          diamt2 *= lum;
        }
        if (fade) {
          float dist = (float)Math.sqrt(sq(i - width / 2) + sq(j - height / 2)) * .7;
          dist = 1 - dist / Math.min(width, height) * 2;
          if (dist < 0) continue;
          float size = (float)Math.pow(dist, .4);
          diamt2 *= size;
        }
        
        // draw element
        if (circle)
          applet.ellipse(i + dx, j + dy, diamt2, diamt2);
        else applet.rect(i - diamt2 / 2.0f + dx, j - diamt2 / 2.0f + dy, diamt2, diamt2);
      }
    }
  }
}