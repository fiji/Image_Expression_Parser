package fiji.expressionparser;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

public class ImgLibUtils
{

	/**
	 * Copy the given Image of type extending RealType to a FloatType image.
	 * 
	 * @param <T>
	 * @param img
	 * @return a new Img.
	 */
	public static final < T extends RealType< T > > Img< FloatType > copyToFloatTypeImage( final Img< T > img )
	{
		// Create target image
		final long[] dimensions = new long[ img.numDimensions() ];
		img.dimensions( dimensions );
		final Img< FloatType > target = new ArrayImgFactory< FloatType >()
				.create( dimensions, new FloatType() );
		// Check if all Containers are compatibles
		final boolean compatible_containers = Util.equalIterationOrder( img, target );

		if ( compatible_containers )
		{

			final Cursor< T > ic = img.cursor();
			final Cursor< FloatType > tc = target.cursor();
			while ( ic.hasNext() )
			{
				ic.fwd();
				tc.fwd();
				tc.get().set( ic.get().getRealFloat() );
			}

		}
		else
		{

			final Cursor< FloatType > tc = target.localizingCursor();
			final RandomAccess< T > ic = img.randomAccess();
			while ( tc.hasNext() )
			{
				tc.fwd();
				ic.setPosition( tc );
				tc.get().set( ic.get().getRealFloat() );
			}

		}

		return target;
	}

}
