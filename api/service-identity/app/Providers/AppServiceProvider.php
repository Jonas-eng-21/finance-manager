<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;
use App\Domain\User\UserRepositoryInterface;
use App\Infrastructure\Persistence\Eloquent\EloquentUserRepository;
use App\Application\Contracts\JwtServiceInterface;
use App\Infrastructure\Services\JwtAuthService;
use App\Application\Contracts\EventDispatcherInterface;
use App\Infrastructure\Events\LaravelEventDispatcher;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        $this->app->bind(UserRepositoryInterface::class, EloquentUserRepository::class);
        $this->app->bind(JwtServiceInterface::class, JwtAuthService::class);
        $this->app->bind(
            EventDispatcherInterface::class,
            LaravelEventDispatcher::class
        );
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        //
    }
}
