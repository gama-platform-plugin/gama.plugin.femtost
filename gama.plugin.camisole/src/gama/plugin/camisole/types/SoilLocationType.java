package gama.plugin.camisole.types;

import gama.annotations.type;
import gama.annotations.support.ISymbolKind;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.types.GamaType;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.ITypesManager;
import gama.api.gaml.types.Types;
import gama.api.runtime.scope.IScope;
import gama.api.types.list.IList;

@type (
		name = ISoilLocationType.TYPE_NAME,
		id = SoilLocation.Id,
		kind = ISymbolKind.REGULAR,
		wraps = { SoilLocation.class })
public class SoilLocationType extends GamaType<SoilLocation> {

	public SoilLocationType(ITypesManager typesManager) {
		super(typesManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canCastToConst() {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public SoilLocation cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof SoilLocation) {
			return new SoilLocation(((SoilLocation) obj).getX(), ((SoilLocation) obj).getY(),
					((SoilLocation) obj).getZ(), ((SoilLocation) obj).getScale());
		} // TODO Auto-generated method stub
		if (obj instanceof IList) {
			final IType<?> mtype = ((IList<?>) obj).getGamlType();
			if (mtype.getKeyType() == Types.INT) {
				final IList<Integer> tt = (IList<Integer>) obj;

			}

			// if(((GamaList)obj).)
		}

		return null;
	}

	@Override
	public SoilLocation getDefault() {
		// TODO Auto-generated method stub
		return null;
	}

}
