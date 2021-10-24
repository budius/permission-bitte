package com.budius.permissionbitte.internal

import android.content.pm.PackageInfo
import android.content.pm.PermissionInfo
import com.budius.permissionbitte.PermissionState
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ManifestMapperTest {

    @Test
    fun `parsePermissions should return null when protection not dangerous`() {
        val tested = "tested"
        val result = ManifestMapper.parsePermissions(
            names = arrayOf("not-testing-1", tested),
            flags = intArrayOf(
                PackageInfo.REQUESTED_PERMISSION_GRANTED,
                PackageInfo.REQUESTED_PERMISSION_GRANTED
            ),
            protection = listOf(
                PermissionInfo.PROTECTION_DANGEROUS,
                PermissionInfo.PROTECTION_NORMAL
            ),
            rationale = listOf(false, false)
        )[tested]
        assertThat(result).isNull()
    }

    @Test
    fun `parsePermissions should return granted when permission is granted`() {
        val tested = "tested"
        val result = ManifestMapper.parsePermissions(
            names = arrayOf(tested, "not-testing-2"),
            flags = intArrayOf(
                PackageInfo.REQUESTED_PERMISSION_GRANTED,
                PackageInfo.REQUESTED_PERMISSION_GRANTED - 1
            ),
            protection = listOf(
                PermissionInfo.PROTECTION_DANGEROUS,
                PermissionInfo.PROTECTION_DANGEROUS
            ),
            rationale = listOf(false, false)
        )[tested]
        assertThat(result).isEqualTo(PermissionState.GRANTED)
    }

    @Test
    fun `parsePermissions should return rationale when permission is not granted and rationale`() {
        val tested = "tested"
        val result = ManifestMapper.parsePermissions(
            names = arrayOf(tested, "not-testing-2"),
            flags = intArrayOf(
                PackageInfo.REQUESTED_PERMISSION_GRANTED - 1,
                PackageInfo.REQUESTED_PERMISSION_GRANTED
            ),
            protection = listOf(
                PermissionInfo.PROTECTION_DANGEROUS,
                PermissionInfo.PROTECTION_DANGEROUS
            ),
            rationale = listOf(true, false)
        )[tested]
        assertThat(result).isEqualTo(PermissionState.SHOW_RATIONALE)
    }

    @Test
    fun `parsePermissions should return request when not granted and rationale is false`() {
        val tested = "tested"
        val result = ManifestMapper.parsePermissions(
            names = arrayOf("not-testing-1", tested),
            flags = intArrayOf(
                PackageInfo.REQUESTED_PERMISSION_GRANTED,
                PackageInfo.REQUESTED_PERMISSION_GRANTED - 1
            ),
            protection = listOf(
                PermissionInfo.PROTECTION_DANGEROUS,
                PermissionInfo.PROTECTION_DANGEROUS
            ),
            rationale = listOf(false, false)
        )[tested]
        assertThat(result).isEqualTo(PermissionState.REQUEST_PERMISSION)
    }

    @Test
    fun `mergePermission should return new if current is null`() {
        val result = ManifestMapper.mergePermission(
            null,
            mapOf(
                "foo" to PermissionState.GRANTED,
                "bar" to PermissionState.SHOW_RATIONALE
            )
        )
        assertThat(result).hasSize(2)
        assertThat(result["foo"]).isEqualTo(PermissionState.GRANTED)
        assertThat(result["bar"]).isEqualTo(PermissionState.SHOW_RATIONALE)
    }

    @Test
    fun `mergePermission should use current state when new has no matching state`() {
        val result = ManifestMapper.mergePermission(
            current = mapOf(
                "foo" to PermissionState.GRANTED,
                "bar" to PermissionState.SHOW_RATIONALE
            ),
            new = mapOf("bar" to PermissionState.GRANTED)
        )
        assertThat(result["foo"]).isEqualTo(PermissionState.GRANTED)
    }

    @Test
    fun `mergePermission should use GRANTED state when new is GRANTED`() {
        val result = ManifestMapper.mergePermission(
            current = mapOf("foo" to PermissionState.DENIED),
            new = mapOf("foo" to PermissionState.GRANTED)
        )
        assertThat(result["foo"]).isEqualTo(PermissionState.GRANTED)
    }

    @Test
    fun `mergePermission should use DENIED state when new is DENIED`() {
        val result = ManifestMapper.mergePermission(
            current = mapOf("foo" to PermissionState.GRANTED),
            new = mapOf("foo" to PermissionState.DENIED)
        )
        assertThat(result["foo"]).isEqualTo(PermissionState.DENIED)
    }

    @Test
    fun `mergePermission should use SHOW_RATIONALE when is DENIED and new is SHOW_RATIONALE`() {
        val result = ManifestMapper.mergePermission(
            current = mapOf("foo" to PermissionState.DENIED),
            new = mapOf("foo" to PermissionState.SHOW_RATIONALE)
        )
        assertThat(result["foo"]).isEqualTo(PermissionState.SHOW_RATIONALE)
    }

    @Test
    fun `mergePermission should use DENIED state when current is DENIED and new is not GRANTED`() {
        val result = ManifestMapper.mergePermission(
            current = mapOf("foo" to PermissionState.DENIED),
            new = mapOf("foo" to PermissionState.REQUEST_PERMISSION)
        )
        assertThat(result["foo"]).isEqualTo(PermissionState.DENIED)
    }

    @Test
    fun `mergePermission should use new in other cases`() {
        val result = ManifestMapper.mergePermission(
            current = mapOf("foo" to PermissionState.REQUEST_PERMISSION),
            new = mapOf("foo" to PermissionState.SHOW_RATIONALE)
        )
        assertThat(result["foo"]).isEqualTo(PermissionState.SHOW_RATIONALE)
    }
}
