package ru.itmo.mpi.api.plugins

import io.ktor.application.Application
import io.ktor.application.install
import org.apache.commons.dbcp2.DriverManagerConnectionFactory
import org.apache.commons.dbcp2.PoolableConnectionFactory
import org.apache.commons.dbcp2.PoolingDataSource
import org.apache.commons.pool2.impl.GenericObjectPool
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.SLF4JLogger
import org.ktorm.database.Database
import ru.itmo.mpi.auth.AuthProviderBase
import ru.itmo.mpi.auth.login_password.LoginPasswordAuthProvider
import ru.itmo.mpi.auth.login_password.PBKDF2WithHmacSHA1PasswordHasher
import ru.itmo.mpi.auth.login_password.PasswordHasher
import ru.itmo.mpi.dal.FileRepository
import ru.itmo.mpi.dal.LoginPasswordRepository
import ru.itmo.mpi.dal.MemoryRepository
import ru.itmo.mpi.dal.UserRepository
import ru.itmo.mpi.dal.db.DbFileRepository
import ru.itmo.mpi.dal.db.DbLoginPasswordRepository
import ru.itmo.mpi.dal.db.DbMemoryRepository
import ru.itmo.mpi.dal.db.DbUserRepository

val insideOutModule = module {
    val connectionFactory = DriverManagerConnectionFactory(
        "jdbc:postgresql://localhost:5433/inside-out",
        "s310279",
        "ohl234"
    )
    val poolableConnectionFactory = PoolableConnectionFactory(connectionFactory, null)
    val connectionPool = GenericObjectPool(poolableConnectionFactory)
    val dataSource = PoolingDataSource(connectionPool)

    factory<LoginPasswordAuthProvider> { LoginPasswordAuthProvider(get(), get(), get()) }
    factory<AuthProviderBase> { LoginPasswordAuthProvider(get(), get(), get()) }
    factory<PasswordHasher> { PBKDF2WithHmacSHA1PasswordHasher() }

    factory<UserRepository> { DbUserRepository(get()) }
    factory<LoginPasswordRepository> { DbLoginPasswordRepository(get()) }
    factory<FileRepository> { DbFileRepository(get()) }
    factory<MemoryRepository> { DbMemoryRepository(get()) }

    factory<Database> { Database.connect(dataSource) }
}

fun Application.configureKoin() {
    install(Koin) {
        SLF4JLogger()
        modules(insideOutModule)
    }
}
