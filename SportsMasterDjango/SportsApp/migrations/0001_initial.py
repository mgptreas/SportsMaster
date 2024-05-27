# Generated by Django 5.0.6 on 2024-05-24 16:51

import django.db.models.deletion
from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Sports',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
                ('field1', models.CharField(blank=True, max_length=255, null=True)),
                ('field2', models.CharField(blank=True, max_length=255, null=True)),
                ('field3', models.CharField(blank=True, max_length=255, null=True)),
                ('field4', models.CharField(blank=True, max_length=255, null=True)),
                ('field5', models.CharField(blank=True, max_length=255, null=True)),
            ],
            options={
                'db_table': 'Sports',
            },
        ),
        migrations.CreateModel(
            name='Users',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('username', models.CharField(max_length=255)),
                ('name', models.CharField(blank=True, max_length=255, null=True)),
                ('email', models.EmailField(blank=True, max_length=254, null=True)),
                ('height', models.FloatField(blank=True, null=True)),
                ('weight', models.FloatField(blank=True, null=True)),
                ('points', models.IntegerField(blank=True, null=True)),
                ('premium', models.BooleanField(default=False)),
            ],
            options={
                'db_table': 'Users',
            },
        ),
        migrations.CreateModel(
            name='Exercises',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('description', models.TextField()),
                ('video', models.CharField(blank=True, max_length=255, null=True)),
                ('difficulty', models.IntegerField()),
                ('field1', models.IntegerField(blank=True, null=True)),
                ('field2', models.IntegerField(blank=True, null=True)),
                ('field3', models.IntegerField(blank=True, null=True)),
                ('field4', models.IntegerField(blank=True, null=True)),
                ('field5', models.IntegerField(blank=True, null=True)),
                ('sport', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='SportsApp.sports')),
            ],
            options={
                'db_table': 'Exercises',
            },
        ),
        migrations.CreateModel(
            name='UserAuth',
            fields=[
                ('user', models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, primary_key=True, serialize=False, to='SportsApp.users')),
                ('username', models.CharField(max_length=255)),
                ('password', models.CharField(max_length=255)),
            ],
            options={
                'db_table': 'UserAuth',
            },
        ),
        migrations.CreateModel(
            name='Analytical',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('timestamp', models.CharField(max_length=255)),
                ('toc', models.IntegerField()),
                ('challenging', models.IntegerField()),
                ('feedback', models.IntegerField()),
                ('exercise', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='SportsApp.exercises')),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='SportsApp.users')),
            ],
            options={
                'db_table': 'Analytical',
            },
        ),
        migrations.CreateModel(
            name='Unlocked',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('exercise', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='SportsApp.exercises')),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='SportsApp.users')),
            ],
            options={
                'db_table': 'Unlocked',
                'unique_together': {('user', 'exercise')},
            },
        ),
        migrations.CreateModel(
            name='Aggregated',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('avgTOC', models.FloatField()),
                ('avgChallenging', models.FloatField()),
                ('avgFeedback', models.FloatField()),
                ('commonness', models.IntegerField()),
                ('rarity', models.IntegerField()),
                ('CoT', models.IntegerField()),
                ('exercise', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='SportsApp.exercises')),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='SportsApp.users')),
            ],
            options={
                'db_table': 'Aggregated',
                'unique_together': {('user', 'exercise')},
            },
        ),
    ]