import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ConversionTables
{
	private static PaintToType[] tablePaintType = 
			{		new PaintToType(Paint.valueOf(Color.WHITE.toString()), TerrainTile.Type.Blank,""),//White to Blank
					new PaintToType(Paint.valueOf(Color.PALEGREEN.toString()), TerrainTile.Type.Plains,"``"),//PaleGreen to Plains
					new PaintToType(Paint.valueOf(Color.DARKGREEN.toString()), TerrainTile.Type.Forest,"T"),//DarkGreen to Forest
					new PaintToType(Paint.valueOf("#79695f"), TerrainTile.Type.Mountain,"^^"),//#79695f to Mountain
					new PaintToType(Paint.valueOf("#544040"), TerrainTile.Type.Peak,"/|\\"),//#544040 to Peak
					new PaintToType(Paint.valueOf(Color.AQUA.toString()), TerrainTile.Type.Water,"~~"),//Aqua to Water
					new PaintToType(Paint.valueOf(Color.GREY.toString()), TerrainTile.Type.Fort,"[ ]"),//Grey to Fort
					new PaintToType(Paint.valueOf(Color.SLATEGRAY.toString()), TerrainTile.Type.Wall,"|-|")//SlateGray to Wall
			};
	
	public static PaintToType convertPaintToType(Paint p)
	{
		for(PaintToType i: tablePaintType)
			if(i.getPaint().equals(p))
				return i;
		
		return tablePaintType[0];
	}
	
	public static PaintToType convertTypeToPaint(TerrainTile.Type t)
	{
		for(PaintToType i: tablePaintType)
			if(i.getType().equals(t))
				return i;
		
		return tablePaintType[0];
	}
	
	public static class PaintToType
	{
		private Paint paint;
		private TerrainTile.Type type;
		private String identifier;
		
		public PaintToType(Paint p, TerrainTile.Type t, String id)
		{
			paint = p;
			type = t;
			identifier = id;
		}

		public Paint getPaint() {
			return paint;
		}

		public TerrainTile.Type getType() {
			return type;
		}
		
		public String getIdentifier()
		{
			return identifier;
		}
	}
}
