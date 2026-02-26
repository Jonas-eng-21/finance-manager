<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;
use App\Domain\User\UserRepositoryInterface;
use App\Infrastructure\Persistence\Eloquent\EloquentUserRepository;
use App\Application\Contracts\JwtServiceInterface;
use App\Infrastructure\Services\JwtAuthService;
use App\Application\Contracts\EventDispatcherInterface;
use App\Infrastructure\Events\LaravelEventDispatcher;
use App\Domain\Auth\RefreshTokenRepositoryInterface;
use App\Infrastructure\Persistence\Eloquent\EloquentRefreshTokenRepository;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public array $bindings = [
        UserRepositoryInterface::class => EloquentUserRepository::class,
        JwtServiceInterface::class => JwtAuthService::class,
        EventDispatcherInterface::class => LaravelEventDispatcher::class,
        RefreshTokenRepositoryInterface::class => EloquentRefreshTokenRepository::class,
    ];

    public function register(): void
    {
    }

    public function boot(): void
    {

    }
}
