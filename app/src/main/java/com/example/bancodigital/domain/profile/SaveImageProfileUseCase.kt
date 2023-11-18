package com.example.bancodigital.domain.profile

import com.example.bancodigital.data.repository.profile.ProfileDataSourceImpl
import javax.inject.Inject

class SaveImageProfileUseCase @Inject constructor(
    private val profileDataSourceImpl: ProfileDataSourceImpl
) {
    suspend operator fun invoke(imageProfile: String) : String{
        return profileDataSourceImpl.saveImage(imageProfile)
    }
}