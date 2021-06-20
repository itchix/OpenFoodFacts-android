package openfoodfacts.github.scrachx.openfood.models.entities.additive

import com.google.common.truth.Truth.assertThat
import openfoodfacts.github.scrachx.openfood.models.DaoSession
import org.greenrobot.greendao.DaoException
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as mockitoWhen

/**
 * Tests for [Additive]
 */
@RunWith(MockitoJUnitRunner::class)
class AdditiveTest {
    @Mock
    private val mockDaoSession: DaoSession? = null

    @Mock
    private lateinit var mockAdditiveDao: AdditiveDao

    @Mock
    private lateinit var mockAdditiveNameDao: AdditiveNameDao
    private lateinit var mAdditive: Additive

    @Before
    fun setup() {
        mockitoWhen(mockDaoSession!!.additiveNameDao).thenReturn(mockAdditiveNameDao)
        mockitoWhen(mockDaoSession.additiveDao).thenReturn(mockAdditiveDao)
        mockitoWhen(mockAdditiveNameDao._queryAdditive_Names(ArgumentMatchers.any()))
                .thenReturn(listOf(ADDITIVE_NAME_1, ADDITIVE_NAME_2))
        mAdditive = Additive()
    }

    @Test
    fun getNamesWithNullNamesAndNullDaoSession_throwsDaoException() {
        assertThrows(DaoException::class.java) { mAdditive.names }
    }

    @Test
    fun getNamesWithNullNamesAndNonNullDaoSession_setsNamesFromAdditiveNamesDao() {
        mAdditive.__setDaoSession(mockDaoSession)
        val names = mAdditive.names
        assertThat(names).hasSize(2)
        assertThat(names[0]!!.name).isEqualTo(ADDITIVE_NAME_NAME_1)
        assertThat(names[1]!!.name).isEqualTo(ADDITIVE_NAME_NAME_2)
    }

    @Test
    fun deleteWithNullDao_throwsDaoException() {
        assertThrows(DaoException::class.java) { mAdditive.delete() }
    }

    @Test
    fun deleteWithNonNullDao_callsDeleteOnDao() {
        mAdditive.__setDaoSession(mockDaoSession)
        mAdditive.delete()
        Mockito.verify(mockAdditiveDao).delete(mAdditive)
    }

    @Test
    fun refreshWithNullDao_throwsDaoException() {
        assertThrows(DaoException::class.java) { mAdditive.refresh() }
    }

    @Test
    fun refreshWithNonNullDao_callsRefreshOnDao() {
        mAdditive.__setDaoSession(mockDaoSession)
        mAdditive.refresh()
        Mockito.verify(mockAdditiveDao)?.refresh(mAdditive)
    }

    @Test
    fun updateWithNullDao_throwsDaoException() {
        assertThrows(DaoException::class.java) { mAdditive.update() }
    }

    @Test
    fun updateWithNonNullDao_callsUpdateOnDao() {
        mAdditive.__setDaoSession(mockDaoSession)
        mAdditive.update()
        Mockito.verify(mockAdditiveDao)?.update(mAdditive)
    }

    companion object {
        private const val ADDITIVE_NAME_NAME_1 = "AdditiveName"
        private const val ADDITIVE_NAME_NAME_2 = "AdditiveName2"
        private val ADDITIVE_NAME_1 = AdditiveName(ADDITIVE_NAME_NAME_1)
        private val ADDITIVE_NAME_2 = AdditiveName(ADDITIVE_NAME_NAME_2)
    }
}