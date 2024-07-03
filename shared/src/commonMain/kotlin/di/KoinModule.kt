import data.repository.Repository
import data.source.DataSource
import data.source.remote.RemoteDataSource
import data.source.remote.Service
import domain.usecase.FetchUseCase
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

private val apiModule = module {
    factory { Service() }
}

private val repositoryModule = module {
    single { Repository() }
    factory<DataSource.Remote> { RemoteDataSource(get()) }
}

private val useCaseModule = module {
    factory { FetchUseCase() }
}

private val sharedModules = listOf(useCaseModule, repositoryModule, apiModule)

fun initKoin(appDeclaration: KoinAppDeclaration) = startKoin {
    modules(sharedModules)
    appDeclaration()
}

fun initKoin() = initKoin { }
