package mirrg.beryllium.event;

import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * 多種のイベントを統合的に扱うことのできるイベントプロバイダです。
 * このイベントプロバイダには、型引数Eのサブクラスであるあらゆるイベントが流れます。
 * 利用者は、 {@link #register(Class, Consumer)} を使って特定のイベントのリスナーを登録することができます。
 * リスナーは、登録時に指定したクラスのインスタンスであるイベントしか受理しません。
 */
public interface IEventProvider<E>
{

	/**
	 * イベントリスナーは、このメソッドによって登録した順番に呼び出されます。
	 * このメソッドによるリスナー登録を解除するには、戻り値であるリムーバーを使用します。
	 */
	public <E2 extends E> Runnable register(Class<E2> clazz, Consumer<E2> listener);

	public void fire(E event);

	//

	public static <E> IEventProvider<E> createInstance()
	{
		return new IEventProvider<>() {

			TreeMap<Integer, Entry<? extends E>> listeners = new TreeMap<>();

			int index = 0;

			@Override
			public <E2 extends E> Runnable register(Class<E2> clazz, Consumer<E2> listener)
			{
				int index2 = index;
				listeners.put(index, new Entry<>(clazz, listener));
				index++;
				return () -> listeners.remove(index2);
			}

			@Override
			public void fire(E event)
			{
				for (Entry<? extends E> entry : listeners.values()) {
					entry.fire(event);
				}
			}

			class Entry<E2>
			{

				public final Class<E2> clazz;
				public final Consumer<E2> listener;

				public Entry(Class<E2> clazz, Consumer<E2> listener)
				{
					this.clazz = clazz;
					this.listener = listener;
				}

				@SuppressWarnings("unchecked")
				public void fire(E event)
				{
					if (clazz.isInstance(event)) {
						listener.accept((E2) event);
					}
				}

			}

		};
	}

}
