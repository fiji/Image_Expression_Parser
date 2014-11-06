package fiji.expressionparser;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class ImgLibUtils  {

	/**
	 * Copy the given Image of type extending RealType to a FloatType image.
	 * @param <T>
	 * @param img
	 * @return
	 */
	public static final <T extends RealType<T>> Img<FloatType> copyToFloatTypeImage(Img<T> img) {
		// Create target image
		final long[] dimensions = new long[img.numDimensions()];
		img.dimensions(dimensions);
		Img<FloatType> target = new ArrayImgFactory<FloatType>()
			.create(dimensions, new FloatType());
		// Check if all Containers are compatibles
		boolean compatible_containers = img.equalIterationOrder(target);

		if (compatible_containers) {

			Cursor<T> ic = img.cursor();
			Cursor<FloatType> tc = target.cursor();
			while (ic.hasNext()) {
				ic.fwd();
				tc.fwd();
				tc.get().set( ic.get().getRealFloat() );
			}

		} else {

			Cursor<FloatType> tc = target.localizingCursor();
			RandomAccess<T> ic = img.randomAccess();
			while (tc.hasNext()) {
				tc.fwd();
				ic.setPosition(tc);
				tc.get().set( ic.get().getRealFloat() );
			}

		}

		return target;
	}


}
