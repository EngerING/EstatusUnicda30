package com.example.estatusunicda30.di


import com.example.estatusunicda30.domain.repo.*
import com.example.estatusunicda30.data.auth.FirebaseAuthRepository
import com.example.estatusunicda30.data.auth.fs.VoteRepositoryImpl
import com.example.estatusunicda30.data.auth.CommentRepositoryImpl
import com.example.estatusunicda30.data.auth.fs.ProfileRepositoryImpl
import com.example.estatusunicda30.data.auth.WorkRepositoryImpl
import com.example.estatusunicda30.data.auth.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Suppress("unused") // Hilt las usa por codegen aunque el IDE diga "never used"
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds @Singleton
    abstract fun bindVoteRepository(impl: VoteRepositoryImpl): VoteRepository

    @Binds @Singleton
    abstract fun bindCommentRepository(impl: CommentRepositoryImpl): CommentRepository

    @Binds @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds @Singleton
    abstract fun bindWorkRepository(
        impl: com.example.estatusunicda30.data.auth.WorkRepositoryImpl
    ): com.example.estatusunicda30.domain.repo.WorkRepository


    @Binds @Singleton abstract fun bindSettingsRepository(impl: SettingsRepository): SettingsRepository
}